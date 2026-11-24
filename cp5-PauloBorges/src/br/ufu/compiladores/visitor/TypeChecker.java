package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;
import br.ufu.compiladores.symbols.SemanticError;
import br.ufu.compiladores.symbols.Symbol;
import br.ufu.compiladores.symbols.SymbolCategory;
import br.ufu.compiladores.types.Type;   // tipo semantico (nao o ast.Type)
import br.ufu.compiladores.types.Types;

import java.util.ArrayList;
import java.util.List;

/*
 * Verificador de tipos (Checkpoint 5, Secao 4 do enunciado).
 *
 * Roda depois da passada de identificacao (CP4), sobre a AST ja decorada com
 * decl. Calcula o tipo de cada expressao (gravado no no), confere comandos,
 * return e chamadas e acumula os erros e os avisos.
 */
public class TypeChecker implements Visitor<Type> {

    private final List<SemanticError> errors = new ArrayList<>();
    private final List<SemanticError> warnings = new ArrayList<>();
    private final RetornaSempre retornaSempre = new RetornaSempre();

    private String funcAtual;       // nome da funcao/procedimento sendo verificado
    private Type retornoAtual;      // tipo de retorno dela (VOID para procedimento)

    // TODO (6.5): variaveis que podem estar sem valor.

    /** Erros de tipo, na ordem em que foram encontrados. */
    public List<SemanticError> getErrors() {
        return errors;
    }

    /** Avisos (variaveis que podem estar sem valor). */
    public List<SemanticError> getWarnings() {
        return warnings;
    }

    /** Registra um erro; o formato das mensagens esta no README. */
    private void erro(int line, String message) {
        errors.add(new SemanticError(line, message));
    }

    /** Registra um aviso: "linha N: aviso: mensagem". */
    private void aviso(int line, String message) {
        warnings.add(new SemanticError(line, "aviso: " + message));
    }

    // --- Helpers de comandos e chamadas ---

    private void condicao(Expr cond, String comando) {
        Type t = cond.accept(this);
        if (t != Type.BOOL && t != Type.ERROR) {
            erro(cond.line(), "condição de '" + comando + "' deve ser bool, mas é " + t);
        }
    }

    private String nomeCategoria(SymbolCategory cat) {
        switch (cat) {
            case CONST: return "constante";
            case FUNC:  return "função";
            case PROC:  return "procedimento";
            default:    return "variável";
        }
    }

    /** Confere se o alvo (de atribuicao ou read) existe e aceita valor. */
    private Type alvo(Symbol decl, String nome, int line) {
        if (decl == null) {
            return Type.ERROR;
        }
        if (decl.category == SymbolCategory.VAR || decl.category == SymbolCategory.PARAM) {
            return decl.type;
        }
        erro(line, "'" + nome + "' é " + nomeCategoria(decl.category) + ": não pode receber valor");
        return Type.ERROR;
    }

    /** Confere a chamada e devolve o tipo de retorno (ERROR se nao der para saber). */
    private Type chamada(Symbol decl, String nome, List<Expr> args, int line) {
        List<Type> argTypes = new ArrayList<>();
        for (Expr arg : args) {
            argTypes.add(arg.accept(this));
        }

        if (decl == null) {
            return Type.ERROR;
        }
        if (decl.category != SymbolCategory.FUNC && decl.category != SymbolCategory.PROC) {
            erro(line, "'" + nome + "' não é função nem procedimento");
            return Type.ERROR;
        }

        Type.FuncType ft = (Type.FuncType) decl.type;
        if (args.size() != ft.params.size()) {
            erro(line, "'" + nome + "' espera " + ft.params.size() + " argumento(s), recebeu " + args.size());
        } else {
            for (int i = 0; i < args.size(); i++) {
                Type esperado = ft.params.get(i);
                Type recebido = argTypes.get(i);
                if (!Types.assignable(esperado, recebido)) {
                    erro(line, "argumento " + (i + 1) + " de '" + nome + "': esperado "
                            + esperado + ", veio " + recebido);
                }
            }
        }
        return ft.returnType;
    }

    // --- Programa e declaracoes ---

    @Override
    public Type visitProgram(Program n) {
        for (Decl d : n.decls) {
            d.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitVarDecl(VarDecl n) {
        if (n.init != null) {
            Type initType = n.init.accept(this);
            Type declType = n.type.accept(this);
            if (!Types.assignable(declType, initType)) {
                erro(n.line(), "tipos incompatíveis na inicialização de '" + n.id + "': "
                        + declType + " recebe " + initType);
            }
        }
        return Type.VOID;
    }

    @Override
    public Type visitFuncDecl(FuncDecl n) {
        funcAtual = n.id;
        retornoAtual = n.type.accept(this);
        for (Cmd c : n.body.cmds) {
            c.accept(this);
        }
        if (retornoAtual != Type.VOID && !n.body.accept(retornaSempre)) {
            erro(n.line(), "função '" + n.id + "' pode terminar sem 'return'");
        }
        return Type.VOID;
    }

    @Override
    public Type visitProcDecl(ProcDecl n) {
        funcAtual = n.id;
        retornoAtual = Type.VOID;
        for (Cmd c : n.body.cmds) {
            c.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitParam(Param n) {
        return Type.VOID;
    }

    // --- Tipos ---

    @Override
    public Type visitPrimitiveType(PrimitiveType n) {
        return Types.of(n.name);
    }

    // --- Comandos ---

    @Override
    public Type visitBlock(BlockCmd n) {
        for (Cmd c : n.cmds) {
            c.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitDeclCmd(DeclCmd n) {
        n.decl.accept(this);
        return Type.VOID;
    }

    @Override
    public Type visitAssign(AssignCmd n) {
        Type exprType = n.expr.accept(this);
        Type targetType = alvo(n.decl, n.id, n.line());
        if (!Types.assignable(targetType, exprType)) {
            erro(n.line(), "tipos incompatíveis na atribuição a '" + n.id + "': "
                    + targetType + " recebe " + exprType);
        }
        return Type.VOID;
    }

    @Override
    public Type visitIf(IfCmd n) {
        condicao(n.cond, "if");
        n.thenCmd.accept(this);
        for (ElifBranch elif : n.elifs) {
            elif.accept(this);
        }
        if (n.elseCmd != null) {
            n.elseCmd.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitElif(ElifBranch n) {
        condicao(n.cond, "elif");
        n.block.accept(this);
        return Type.VOID;
    }

    @Override
    public Type visitWhile(WhileCmd n) {
        condicao(n.cond, "while");
        n.body.accept(this);
        return Type.VOID;
    }

    @Override
    public Type visitCallCmd(CallCmd n) {
        chamada(n.decl, n.id, n.args, n.line());
        return Type.VOID;
    }

    @Override
    public Type visitReturn(ReturnCmd n) {
        if (n.expr != null) {
            Type t = n.expr.accept(this);
            if (retornoAtual == Type.VOID) {
                erro(n.line(), "procedimento '" + funcAtual + "' não devolve valor");
            } else if (!Types.assignable(retornoAtual, t)) {
                erro(n.line(), "'" + funcAtual + "' devolve " + retornoAtual + ", mas 'return' dá " + t);
            }
        } else {
            if (retornoAtual != Type.VOID) {
                erro(n.line(), "'return' sem valor em '" + funcAtual + "', que devolve " + retornoAtual);
            }
        }
        return Type.VOID;
    }

    @Override
    public Type visitPrint(PrintCmd n) {
        n.expr.accept(this);
        return Type.VOID;
    }

    @Override
    public Type visitRead(ReadCmd n) {
        Type targetType = alvo(n.decl, n.id, n.line());
        if (targetType != Type.ERROR
                && targetType != Type.INT
                && targetType != Type.DOUBLE
                && targetType != Type.CHAR) {
            erro(n.line(), "'read' não aceita '" + n.id + "', que é " + targetType);
        }
        return Type.VOID;
    }

    // --- Expressoes ---

    @Override
    public Type visitIntLiteral(IntLiteral n) {
        return n.type = Type.INT;
    }

    @Override
    public Type visitDoubleLiteral(DoubleLiteral n) {
        return n.type = Type.DOUBLE;
    }

    @Override
    public Type visitBoolLiteral(BoolLiteral n) {
        return n.type = Type.BOOL;
    }

    @Override
    public Type visitCharLiteral(CharLiteral n) {
        return n.type = Type.CHAR;
    }

    @Override
    public Type visitStringLiteral(StringLiteral n) {
        return n.type = Type.STRING;
    }

    @Override
    public Type visitId(IdExpr n) {
        if (n.decl == null) {
            return n.type = Type.ERROR;
        }
        if (n.decl.category == SymbolCategory.FUNC || n.decl.category == SymbolCategory.PROC) {
            erro(n.line(), "'" + n.id + "' é " + nomeCategoria(n.decl.category) + ": não pode ser usado sem parênteses");
            return n.type = Type.ERROR;
        }
        return n.type = n.decl.type;
    }

    @Override
    public Type visitBin(BinExpr n) {
        Type l = n.left.accept(this);
        Type r = n.right.accept(this);
        Type res = Types.binary(n.op, l, r);
        if (res == Type.ERROR && l != Type.ERROR && r != Type.ERROR) {
            erro(n.line(), "tipos incompatíveis em '" + n.op + "': " + l + " e " + r);
        }
        return n.type = res;
    }

    @Override
    public Type visitUn(UnExpr n) {
        Type t = n.expr.accept(this);
        Type res = Types.unary(n.op, t);
        if (res == Type.ERROR && t != Type.ERROR) {
            erro(n.line(), "operando incompatível em '" + n.op + "': " + t);
        }
        return n.type = res;
    }

    @Override
    public Type visitCallExpr(CallExpr n) {
        Type t = chamada(n.decl, n.id, n.args, n.line());
        if (n.decl != null && n.decl.category == SymbolCategory.PROC) {
            erro(n.line(), "'" + n.id + "' é procedimento e não devolve valor");
            t = Type.ERROR;
        }
        return n.type = t;
    }
}