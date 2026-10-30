package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class WhileCmd extends Cmd {

    public final Expr cond;
    public final BlockCmd body;

    public WhileCmd(int line, int col, Expr cond, BlockCmd body) {
        super(line, col);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitWhile(this);
    }
}
