package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;

/*
 * Imprime as resoluções de nomes após a passada de identificação.
 * Lê o campo 'decl' dos nós de uso e formata como: nome@linhaDoUso -> nome@linhaDaDeclaração.
 */
public class ResolutionPrinter implements Visitor<Void> {

    private final StringBuilder sb = new StringBuilder();

    public String getOutput() {
        return sb.toString();
    }

    // --- Usos diretos de identificadores ---

    @Override
    public Void visitId(IdExpr n) {
        if (n.decl != null) {
            sb.append(n.id).append("@").append(n.line()).append(" -> ").append(n.decl).append("\n");
        }
        return null;
    }

    @Override
    public Void visitRead(ReadCmd n) {
        if (n.decl != null) {
            sb.append(n.id).append("@").append(n.line()).append(" -> ").append(n.decl).append("\n");
        }
        return null;
    }

    @Override
    public Void visitAssign(AssignCmd n) {
        if (n.decl != null) {
            sb.append(n.id).append("@").append(n.line()).append(" -> ").append(n.decl).append("\n");
        }
        n.expr.accept(this);
        return null;
    }

    @Override
    public Void visitCallCmd(CallCmd n) {
        if (n.decl != null) {
            sb.append(n.id).append("@").append(n.line()).append(" -> ").append(n.decl).append("\n");
        }
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr n) {
        if (n.decl != null) {
            sb.append(n.id).append("@").append(n.line()).append(" -> ").append(n.decl).append("\n");
        }
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        return null;
    }

    // --- Estruturas e Declarações ---

    @Override
    public Void visitProgram(Program n) {
        for (Decl d : n.decls) {
            d.accept(this);
        }
        return null;
    }

    @Override
    public Void visitBlock(BlockCmd n) {
        for (Cmd c : n.cmds) {
            c.accept(this);
        }
        return null;
    }

    @Override
    public Void visitFuncDecl(FuncDecl n) {
        for (Param p : n.params) {
            p.accept(this);
        }
        n.body.accept(this);
        return null;
    }

    @Override
    public Void visitProcDecl(ProcDecl n) {
        for (Param p : n.params) {
            p.accept(this);
        }
        n.body.accept(this);
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl n) {
        if (n.init != null) {
            n.init.accept(this);
        }
        return null;
    }

    // --- Comandos ---

    @Override
    public Void visitDeclCmd(DeclCmd n) {
        n.decl.accept(this);
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

    // --- Expressões ---

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

    // --- Nós vazios (não contém usos de identificadores que precisamos imprimir) ---

    @Override
    public Void visitParam(Param n) { return null; }

    @Override
    public Void visitPrimitiveType(PrimitiveType n) { return null; }

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
}