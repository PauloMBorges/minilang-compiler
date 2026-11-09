package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;

/**
 * Reimpressor da AST. Produz o código-fonte na forma canônica,
 * garantindo a mesma árvore sintática abstrata no round-trip.
 */
public class AstUnparser implements Visitor<Void> {

    private StringBuilder sb = new StringBuilder();
    private int depth = 0;

    public String getOutput() {
        return sb.toString();
    }

    private void printIndent() {
        for (int i = 0; i < depth; i++) {
            sb.append("    "); // 4 espaços para indentação do código canônico
        }
    }

    // --- Programa e declaracoes ---

    @Override
    public Void visitProgram(Program n) {
        for (Decl decl : n.decls) {
            decl.accept(this);
            sb.append("\n"); // Linha extra entre as declarações globais/funções
        }
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl n) {
        printIndent();
        if (n.isConst) {
            sb.append("const ");
        }
        n.type.accept(this);
        sb.append(" ").append(n.id);
        if (n.init != null) {
            sb.append(" = ");
            n.init.accept(this);
        }
        sb.append(";\n");
        return null;
    }

    @Override
    public Void visitFuncDecl(FuncDecl n) {
        printIndent();
        n.type.accept(this);
        sb.append(" ").append(n.id).append("(");
        for (int i = 0; i < n.params.size(); i++) {
            n.params.get(i).accept(this);
            if (i < n.params.size() - 1) sb.append(", ");
        }
        sb.append(") ");
        n.body.accept(this);
        sb.append("\n");
        return null;
    }

    @Override
    public Void visitProcDecl(ProcDecl n) {
        printIndent();
        sb.append("void ").append(n.id).append("(");
        for (int i = 0; i < n.params.size(); i++) {
            n.params.get(i).accept(this);
            if (i < n.params.size() - 1) sb.append(", ");
        }
        sb.append(") ");
        n.body.accept(this);
        sb.append("\n");
        return null;
    }

    @Override
    public Void visitParam(Param n) {
        n.type.accept(this);
        sb.append(" ").append(n.id);
        return null;
    }

    // --- Tipos ---

    @Override
    public Void visitPrimitiveType(PrimitiveType n) {
        sb.append(n.name);
        return null;
    }

    // --- Comandos ---

    @Override
    public Void visitBlock(BlockCmd n) {
        sb.append("{\n");
        depth++;
        for (Cmd cmd : n.cmds) {
            cmd.accept(this);
        }
        depth--;
        printIndent();
        sb.append("}");
        return null;
    }

    @Override
    public Void visitDeclCmd(DeclCmd n) {
        n.decl.accept(this); // O VarDecl já faz o printIndent e acrescenta o ;\n
        return null;
    }

    @Override
    public Void visitAssign(AssignCmd n) {
        printIndent();
        sb.append(n.id).append(" = ");
        n.expr.accept(this);
        sb.append(";\n");
        return null;
    }

    @Override
    public Void visitIf(IfCmd n) {
        printIndent();
        sb.append("if (");
        n.cond.accept(this);
        sb.append(") ");
        n.thenCmd.accept(this);
        for (ElifBranch elif : n.elifs) {
            elif.accept(this);
        }
        if (n.elseCmd != null) {
            sb.append(" else ");
            n.elseCmd.accept(this);
        }
        sb.append("\n");
        return null;
    }

    @Override
    public Void visitElif(ElifBranch n) {
        sb.append(" elif (");
        n.cond.accept(this);
        sb.append(") ");
        n.block.accept(this);
        return null;
    }

    @Override
    public Void visitWhile(WhileCmd n) {
        printIndent();
        sb.append("while (");
        n.cond.accept(this);
        sb.append(") ");
        n.body.accept(this);
        sb.append("\n");
        return null;
    }

    @Override
    public Void visitCallCmd(CallCmd n) {
        printIndent();
        sb.append(n.id).append("(");
        for (int i = 0; i < n.args.size(); i++) {
            n.args.get(i).accept(this);
            if (i < n.args.size() - 1) sb.append(", ");
        }
        sb.append(");\n");
        return null;
    }

    @Override
    public Void visitReturn(ReturnCmd n) {
        printIndent();
        sb.append("return");
        if (n.expr != null) {
            sb.append(" ");
            n.expr.accept(this);
        }
        sb.append(";\n");
        return null;
    }

    @Override
    public Void visitPrint(PrintCmd n) {
        printIndent();
        sb.append("print(");
        n.expr.accept(this);
        sb.append(");\n");
        return null;
    }

    @Override
    public Void visitRead(ReadCmd n) {
        printIndent();
        sb.append("read(").append(n.id).append(");\n");
        return null;
    }

    // --- Expressoes ---

    @Override
    public Void visitIntLiteral(IntLiteral n) {
        sb.append(n.lexeme); // Usa o lexema para manter underscores (ex: 10_000)
        return null;
    }

    @Override
    public Void visitDoubleLiteral(DoubleLiteral n) {
        sb.append(n.lexeme);
        return null;
    }

    @Override
    public Void visitBoolLiteral(BoolLiteral n) {
        sb.append(n.value); // false ou true nativos
        return null;
    }

    @Override
    public Void visitCharLiteral(CharLiteral n) {
        sb.append("'").append(n.value).append("'"); // Reconstrói as aspas simples
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteral n) {
        sb.append("\"").append(n.value).append("\""); // Reconstrói as aspas duplas
        return null;
    }

    @Override
    public Void visitId(IdExpr n) {
        sb.append(n.id);
        return null;
    }

    @Override
    public Void visitBin(BinExpr n) {
        // FORMA CANÔNICA: Engloba todas as expressões binárias em parênteses.
        sb.append("(");
        n.left.accept(this);
        sb.append(" ").append(n.op).append(" ");
        n.right.accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visitUn(UnExpr n) {
        // FORMA CANÔNICA: operando sempre entre parênteses. Evita que (-a) ** b
        // volte como -a ** b (que a gramática lê como -(a ** b)) e que - -a vire --a.
        sb.append(n.op).append("(");
        n.expr.accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr n) {
        sb.append(n.id).append("(");
        for (int i = 0; i < n.args.size(); i++) {
            n.args.get(i).accept(this);
            if (i < n.args.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return null;
    }
}