package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;
import br.ufu.compiladores.symbols.SemanticError;
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

    // TODO: contexto (funcao corrente, variaveis que podem estar sem valor).

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
            n.init.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitFuncDecl(FuncDecl n) {
        for (Cmd c : n.body.cmds) {
            c.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visitProcDecl(ProcDecl n) {
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
        return Type.VOID;
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

    @Override public Type visitAssign(AssignCmd n) { /* TODO 6.2 */ return Type.VOID; }
    @Override public Type visitIf(IfCmd n)         { /* TODO 6.2 */ return Type.VOID; }
    @Override public Type visitElif(ElifBranch n)  { /* TODO 6.2 */ return Type.VOID; }
    @Override public Type visitWhile(WhileCmd n)   { /* TODO 6.2 */ return Type.VOID; }
    @Override public Type visitCallCmd(CallCmd n)  { /* TODO 6.3 */ return Type.VOID; }
    @Override public Type visitReturn(ReturnCmd n) { /* TODO 6.3 */ return Type.VOID; }
    @Override public Type visitPrint(PrintCmd n)   { /* TODO 6.2 */ return Type.VOID; }
    @Override public Type visitRead(ReadCmd n)     { /* TODO 6.2 */ return Type.VOID; }

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
            erro(n.line(), "'" + n.id + "' é função: não pode ser usado sem parênteses");
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
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        return n.type = Type.ERROR;
    }
}