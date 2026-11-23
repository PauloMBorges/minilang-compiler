package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.AssignCmd;
import br.ufu.compiladores.ast.BinExpr;
import br.ufu.compiladores.ast.BlockCmd;
import br.ufu.compiladores.ast.BoolLiteral;
import br.ufu.compiladores.ast.CallCmd;
import br.ufu.compiladores.ast.CallExpr;
import br.ufu.compiladores.ast.CharLiteral;
import br.ufu.compiladores.ast.DeclCmd;
import br.ufu.compiladores.ast.DoubleLiteral;
import br.ufu.compiladores.ast.ElifBranch;
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

public interface Visitor<T> {

    // --- Programa e declaracoes ---
    T visitProgram(Program n);
    T visitVarDecl(VarDecl n);
    T visitFuncDecl(FuncDecl n);
    T visitProcDecl(ProcDecl n);
    T visitParam(Param n);

    // --- Tipos ---
    T visitPrimitiveType(PrimitiveType n);

    // --- Comandos ---
    T visitBlock(BlockCmd n);
    T visitDeclCmd(DeclCmd n);
    T visitAssign(AssignCmd n);
    T visitIf(IfCmd n);
    T visitElif(ElifBranch n);
    T visitWhile(WhileCmd n);
    T visitCallCmd(CallCmd n);
    T visitReturn(ReturnCmd n);
    T visitPrint(PrintCmd n);
    T visitRead(ReadCmd n);

    // --- Expressoes ---
    T visitIntLiteral(IntLiteral n);
    T visitDoubleLiteral(DoubleLiteral n);
    T visitBoolLiteral(BoolLiteral n);
    T visitCharLiteral(CharLiteral n);
    T visitStringLiteral(StringLiteral n);
    T visitId(IdExpr n);
    T visitBin(BinExpr n);
    T visitUn(UnExpr n);
    T visitCallExpr(CallExpr n);
}
