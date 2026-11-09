package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class AssignCmd extends Cmd {

    public final String id;
    public final Expr expr;

    public AssignCmd(int line, int col, String id, Expr expr) {
        super(line, col);
        this.id = id;
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitAssign(this);
    }
}
