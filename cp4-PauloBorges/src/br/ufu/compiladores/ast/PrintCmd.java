package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class PrintCmd extends Cmd {

    public final Expr expr;

    public PrintCmd(int line, int col, Expr expr) {
        super(line, col);
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitPrint(this);
    }
}
