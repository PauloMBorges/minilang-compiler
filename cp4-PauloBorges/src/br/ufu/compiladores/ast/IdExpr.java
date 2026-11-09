package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class IdExpr extends Expr {

    public final String id;

    public IdExpr(int line, int col, String id) {
        super(line, col);
        this.id = id;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitId(this);
    }
}
