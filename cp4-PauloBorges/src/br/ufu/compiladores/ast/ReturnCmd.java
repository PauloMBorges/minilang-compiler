package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class ReturnCmd extends Cmd {

    public final Expr expr;   // null em "return;"

    public ReturnCmd(int line, int col, Expr expr) {
        super(line, col);
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitReturn(this);
    }
}
