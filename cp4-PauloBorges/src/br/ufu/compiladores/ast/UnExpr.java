package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class UnExpr extends Expr {

    public final String op;
    public final Expr expr;

    public UnExpr(int line, int col, String op, Expr expr) {
        super(line, col);
        this.op = op;
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitUn(this);
    }
}
