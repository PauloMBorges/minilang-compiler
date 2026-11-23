package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class BinExpr extends Expr {

    public final Expr left;
    public final String op;
    public final Expr right;

    public BinExpr(int line, int col, Expr left, String op, Expr right) {
        super(line, col);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBin(this);
    }
}
