package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;

/*
 * Analise estrutural conservadora de retorno (Checkpoint 5, decisao 9).
 *
 * Responde se um comando garante a execucao de um 'return' em todos os
 * caminhos estruturais, sem usar instanceof
 */
public class RetornaSempre implements Visitor<Boolean> {

    // --- Comandos que podem garantir return ---

    @Override
    public Boolean visitReturn(ReturnCmd n) {
        return true;
    }

    @Override
    public Boolean visitBlock(BlockCmd n) {
        for (Cmd c : n.cmds) {
            if (c.accept(this)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean visitIf(IfCmd n) {
        if (n.elseCmd == null) {
            return false;
        }
        if (!n.thenCmd.accept(this)) {
            return false;
        }
        for (ElifBranch elif : n.elifs) {
            if (!elif.accept(this)) {
                return false;
            }
        }
        return n.elseCmd.accept(this);
    }

    @Override
    public Boolean visitElif(ElifBranch n) {
        return n.block.accept(this);
    }

    // --- Demais comandos (nao garantem return) ---

    @Override public Boolean visitWhile(WhileCmd n)     { return false; }
    @Override public Boolean visitAssign(AssignCmd n)   { return false; }
    @Override public Boolean visitDeclCmd(DeclCmd n)    { return false; }
    @Override public Boolean visitCallCmd(CallCmd n)    { return false; }
    @Override public Boolean visitPrint(PrintCmd n)     { return false; }
    @Override public Boolean visitRead(ReadCmd n)       { return false; }

    // --- Programa, declaracoes e tipos ---

    @Override public Boolean visitProgram(Program n)             { return false; }
    @Override public Boolean visitVarDecl(VarDecl n)             { return false; }
    @Override public Boolean visitFuncDecl(FuncDecl n)           { return false; }
    @Override public Boolean visitProcDecl(ProcDecl n)           { return false; }
    @Override public Boolean visitParam(Param n)                 { return false; }
    @Override public Boolean visitPrimitiveType(PrimitiveType n) { return false; }

    // --- Expressoes ---

    @Override public Boolean visitIntLiteral(IntLiteral n)       { return false; }
    @Override public Boolean visitDoubleLiteral(DoubleLiteral n) { return false; }
    @Override public Boolean visitBoolLiteral(BoolLiteral n)     { return false; }
    @Override public Boolean visitCharLiteral(CharLiteral n)     { return false; }
    @Override public Boolean visitStringLiteral(StringLiteral n) { return false; }
    @Override public Boolean visitId(IdExpr n)                   { return false; }
    @Override public Boolean visitBin(BinExpr n)                 { return false; }
    @Override public Boolean visitUn(UnExpr n)                   { return false; }
    @Override public Boolean visitCallExpr(CallExpr n)           { return false; }
}