package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.AssignCmd;
import br.ufu.compiladores.ast.BinExpr;
import br.ufu.compiladores.ast.BlockCmd;
import br.ufu.compiladores.ast.BoolLiteral;
import br.ufu.compiladores.ast.CallCmd;
import br.ufu.compiladores.ast.CallExpr;
import br.ufu.compiladores.ast.CharLiteral;
import br.ufu.compiladores.ast.Cmd;
import br.ufu.compiladores.ast.Decl;
import br.ufu.compiladores.ast.DeclCmd;
import br.ufu.compiladores.ast.DoubleLiteral;
import br.ufu.compiladores.ast.ElifBranch;
import br.ufu.compiladores.ast.Expr;
import br.ufu.compiladores.ast.FuncDecl;
import br.ufu.compiladores.ast.IdExpr;
import br.ufu.compiladores.ast.IfCmd;
import br.ufu.compiladores.ast.IntLiteral;
import br.ufu.compiladores.ast.Param;
import br.ufu.compiladores.ast.PrimitiveType;
import br.ufu.compiladores.ast.PrintCmd;
import br.ufu.compiladores.ast.ProcDecl;
import br.ufu.compiladores.ast.Program;
import br.ufu.compiladores.ast.ReadCmd;
import br.ufu.compiladores.ast.ReturnCmd;
import br.ufu.compiladores.ast.StringLiteral;
import br.ufu.compiladores.ast.UnExpr;
import br.ufu.compiladores.ast.VarDecl;
import br.ufu.compiladores.ast.WhileCmd;

public class AstPrinter implements Visitor<Void> {
    
    private StringBuilder sb = new StringBuilder();
    private int depth = 0;

    // Retorna a árvore formatada em texto puro.
    public String getOutput() {
        return sb.toString();
    }

    // Auxiliar para imprimir uma linha com a indentação correta
    private void printLine(String text) {
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(text).append("\n");
    }

    // --- Programa e declaracoes ---
    @Override
    public Void visitProgram(Program n) {
        printLine("Program");
        depth++;
        for (Decl decl : n.decls) {
            decl.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl n) {
        printLine("VarDecl(isConst=" + n.isConst + ", id=\"" + n.id + "\")");
        depth++;
        n.type.accept(this);
        if (n.init != null) {
            n.init.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitFuncDecl(FuncDecl n) {
        printLine("FuncDecl(id=\"" + n.id + "\")");
        depth++;
        n.type.accept(this);
        for (Param p : n.params) {
            p.accept(this);
        }
        n.body.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitProcDecl(ProcDecl n) {
        printLine("ProcDecl(id=\"" + n.id + "\")");
        depth++;
        for (Param p : n.params) {
            p.accept(this);
        }
        n.body.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitParam(Param n) {
        printLine("Param(id=\"" + n.id + "\")");
        depth++;
        n.type.accept(this);
        depth--;
        return null;
    }

    // --- Tipos ---
    @Override
    public Void visitPrimitiveType(PrimitiveType n) {
        printLine("PrimitiveType(name=\"" + n.name + "\")");
        return null;
    }

    // --- Comandos ---
    @Override
    public Void visitBlock(BlockCmd n) {
        printLine("BlockCmd");
        depth++;
        for (Cmd c : n.cmds) {
            c.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitDeclCmd(DeclCmd n) {
        printLine("DeclCmd");
        depth++;
        n.decl.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitAssign(AssignCmd n) {
        printLine("AssignCmd(id=\"" + n.id + "\")");
        depth++;
        n.expr.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitIf(IfCmd n) {
        printLine("IfCmd");
        depth++;
        n.cond.accept(this);
        n.thenCmd.accept(this);
        for (ElifBranch elif : n.elifs) {
            elif.accept(this);
        }
        if (n.elseCmd != null) {
            n.elseCmd.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitElif(ElifBranch n) {
        printLine("ElifBranch");
        depth++;
        n.cond.accept(this);
        n.block.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitWhile(WhileCmd n) {
        printLine("WhileCmd");
        depth++;
        n.cond.accept(this);
        n.body.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitCallCmd(CallCmd n) {
        printLine("CallCmd(id=\"" + n.id + "\")");
        depth++;
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitReturn(ReturnCmd n) {
        printLine("ReturnCmd");
        if (n.expr != null) {
            depth++;
            n.expr.accept(this);
            depth--;
        }
        return null;
    }

    @Override
    public Void visitPrint(PrintCmd n) {
        printLine("PrintCmd");
        depth++;
        n.expr.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitRead(ReadCmd n) {
        printLine("ReadCmd(id=\"" + n.id + "\")");
        return null;
    }

    // --- Expressoes ---
    @Override
    public Void visitIntLiteral(IntLiteral n) {
        printLine("IntLiteral(lexeme=\"" + n.lexeme + "\")");
        return null;
    }

    @Override
    public Void visitDoubleLiteral(DoubleLiteral n) {
        printLine("DoubleLiteral(lexeme=\"" + n.lexeme + "\")");
        return null;
    }

    @Override
    public Void visitBoolLiteral(BoolLiteral n) {
        printLine("BoolLiteral(value=" + n.value + ")");
        return null;
    }

    @Override
    public Void visitCharLiteral(CharLiteral n) {
        printLine("CharLiteral(value='" + n.value + "')");
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteral n) {
        printLine("StringLiteral(value=\"" + n.value + "\")");
        return null;
    }

    @Override
    public Void visitId(IdExpr n) {
        printLine("IdExpr(id=\"" + n.id + "\")");
        return null;
    }

    @Override
    public Void visitBin(BinExpr n) {
        printLine("BinExpr(op=\"" + n.op + "\")");
        depth++;
        n.left.accept(this);
        n.right.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitUn(UnExpr n) {
        printLine("UnExpr(op=\"" + n.op + "\")");
        depth++;
        n.expr.accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr n) {
        printLine("CallExpr(id=\"" + n.id + "\")");
        depth++;
        for (Expr arg : n.args) {
            arg.accept(this);
        }
        depth--;
        return null;
    }
}