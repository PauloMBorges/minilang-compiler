package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;

/**
 * Reimpressor da AST (esqueleto para os testes compilarem).
 * Será implementado no próximo passo.
 */
public class AstUnparser implements Visitor<Void> {

    public String getOutput() {
        return ""; // Stub para o código compilar
    }

    @Override public Void visitProgram(Program n) { return null; }
    @Override public Void visitVarDecl(VarDecl n) { return null; }
    @Override public Void visitFuncDecl(FuncDecl n) { return null; }
    @Override public Void visitProcDecl(ProcDecl n) { return null; }
    @Override public Void visitParam(Param n) { return null; }
    @Override public Void visitPrimitiveType(PrimitiveType n) { return null; }
    @Override public Void visitBlock(BlockCmd n) { return null; }
    @Override public Void visitDeclCmd(DeclCmd n) { return null; }
    @Override public Void visitAssign(AssignCmd n) { return null; }
    @Override public Void visitIf(IfCmd n) { return null; }
    @Override public Void visitElif(ElifBranch n) { return null; }
    @Override public Void visitWhile(WhileCmd n) { return null; }
    @Override public Void visitCallCmd(CallCmd n) { return null; }
    @Override public Void visitReturn(ReturnCmd n) { return null; }
    @Override public Void visitPrint(PrintCmd n) { return null; }
    @Override public Void visitRead(ReadCmd n) { return null; }
    @Override public Void visitIntLiteral(IntLiteral n) { return null; }
    @Override public Void visitDoubleLiteral(DoubleLiteral n) { return null; }
    @Override public Void visitBoolLiteral(BoolLiteral n) { return null; }
    @Override public Void visitCharLiteral(CharLiteral n) { return null; }
    @Override public Void visitStringLiteral(StringLiteral n) { return null; }
    @Override public Void visitId(IdExpr n) { return null; }
    @Override public Void visitBin(BinExpr n) { return null; }
    @Override public Void visitUn(UnExpr n) { return null; }
    @Override public Void visitCallExpr(CallExpr n) { return null; }
}