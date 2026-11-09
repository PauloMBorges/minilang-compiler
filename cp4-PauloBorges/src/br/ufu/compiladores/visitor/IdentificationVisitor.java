package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;
import br.ufu.compiladores.symbols.SemanticError;
import br.ufu.compiladores.symbols.StackedSymbolTable;
import br.ufu.compiladores.symbols.Symbol;
import br.ufu.compiladores.symbols.SymbolCategory;
import br.ufu.compiladores.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/*
 * Passada de identificacao (Checkpoint 4, Secao 4 do enunciado).
 *
 * Percorre a AST do CP3 (so roda sobre AST valida), abre e fecha escopos,
 * declara os nomes e decora a arvore: cada identificador de uso passa a
 * apontar para o Symbol da sua declaracao (campo decl).
 */
public class IdentificationVisitor implements Visitor<Void> {

    private final SymbolTable table;
    private final List<SemanticError> errors = new ArrayList<>();

    public IdentificationVisitor() {
        // Unica linha da passada que conhece a classe concreta.
        this.table = new StackedSymbolTable();
    }

    /** Erros na ordem em que foram encontrados (ordem do texto). */
    public List<SemanticError> getErrors() {
        return errors;
    }

    /** Registra um erro; o formato da mensagem esta na secao 5 do escopo-design.md. */
    private void erro(int line, String message) {
        errors.add(new SemanticError(line, message));
    }

    // --- Helpers de Escopo ---

    private void declarar(String nome, SymbolCategory cat, Node no) {
        Symbol s = new Symbol(nome, cat, no, table.level());
        if (!table.declare(s)) {
            Symbol existente = table.lookupLocal(nome);
            erro(no.line(), "'" + nome + "' já declarado neste escopo (linha " + existente.declNode.line() + ")");
        }
    }

    private Symbol resolver(String nome, int linha) {
        Symbol s = table.lookup(nome);
        if (s != null) {
            return s;
        }
        erro(linha, "'" + nome + "' não declarado");
        return null;
    }

    // --- Programa e declaracoes ---

    @Override
    public Void visitProgram(Program n) {
        for (Decl d : n.decls) {
            d.accept(this);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl n) {
        // Visita o init primeiro (regra "resolve antes de declarar")
        if (n.init != null) {
            n.init.accept(this);
        }
        // Depois declara
        SymbolCategory cat = n.isConst ? SymbolCategory.CONST : SymbolCategory.VAR;
        declarar(n.id, cat, n);
        return null;
    }

    @Override
    public Void visitFuncDecl(FuncDecl n) {
        // 1. Declara o nome
        declarar(n.id, SymbolCategory.FUNC, n);
        // 2. Abre o escopo (parâmetros e corpo compartilham o mesmo)
        table.openScope();
        // 3. Visita parâmetros
        for (Param p : n.params) {
            p.accept(this);
        }
        // 4. Visita comandos (diretamente da lista, sem dar accept no body)
        for (Cmd c : n.body.cmds) {
            c.accept(this);
        }
        // 5. Fecha o escopo
        table.closeScope();
        return null;
    }

    @Override
    public Void visitProcDecl(ProcDecl n) {
        declarar(n.id, SymbolCategory.PROC, n);
        table.openScope();
        for (Param p : n.params) {
            p.accept(this);
        }
        for (Cmd c : n.body.cmds) {
            c.accept(this);
        }
        table.closeScope();
        return null;
    }

    @Override
    public Void visitParam(Param n) {
        declarar(n.id, SymbolCategory.PARAM, n);
        return null;
    }

    // --- Tipos ---

    @Override
    public Void visitPrimitiveType(PrimitiveType n) {
        // Tipos primitivos não geram ações de escopo nesta fase
        return null;
    }

    // --- Comandos ---

    @Override
    public Void visitBlock(BlockCmd n) {
        table.openScope();
        for (Cmd c : n.cmds) {
            c.accept(this);
        }
        table.closeScope();
        return null;
    }

    @Override
    public Void visitDeclCmd(DeclCmd n) {
        n.decl.accept(this);
        return null;
    }

    @Override
    public Void visitAssign(AssignCmd n) {
        n.decl = resolver(n.id, n.line());
        n.expr.accept(this);
        return null;
    }

    @Override
    public Void visitIf(IfCmd n) {
        n.cond.accept(this);
        n.thenCmd.accept(this);
        for (ElifBranch elif : n.elifs) {
            elif.accept(this);
        }
        if (n.elseCmd != null) {
            n.elseCmd.accept(this);
        }
        return null;
    }

    @Override
    public Void visitElif(ElifBranch n) {
        n.cond.accept(this);
        n.block.accept(this);
        return null;
    }

    @Override
    public Void visitWhile(WhileCmd n) {
        n.cond.accept(this);
        n.body.accept(this);
        return null;
    }

    @Override
    public Void visitCallCmd(CallCmd n) {
        n.decl = resolver(n.id, n.line());
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitReturn(ReturnCmd n) {
        if (n.expr != null) {
            n.expr.accept(this);
        }
        return null;
    }

    @Override
    public Void visitPrint(PrintCmd n) {
        n.expr.accept(this);
        return null;
    }

    @Override
    public Void visitRead(ReadCmd n) {
        n.decl = resolver(n.id, n.line());
        return null;
    }

    // --- Expressoes ---

    @Override
    public Void visitIntLiteral(IntLiteral n) { return null; }
    
    @Override
    public Void visitDoubleLiteral(DoubleLiteral n) { return null; }
    
    @Override
    public Void visitBoolLiteral(BoolLiteral n) { return null; }
    
    @Override
    public Void visitCharLiteral(CharLiteral n) { return null; }
    
    @Override
    public Void visitStringLiteral(StringLiteral n) { return null; }

    @Override
    public Void visitId(IdExpr n) {
        n.decl = resolver(n.id, n.line());
        return null;
    }

    @Override
    public Void visitBin(BinExpr n) {
        n.left.accept(this);
        n.right.accept(this);
        return null;
    }

    @Override
    public Void visitUn(UnExpr n) {
        n.expr.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr n) {
        n.decl = resolver(n.id, n.line());
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        return null;
    }
}