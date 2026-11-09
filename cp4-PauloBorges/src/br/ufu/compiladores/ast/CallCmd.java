package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class CallCmd extends Cmd {

    public final String id;
    public final List<Expr> args;

    public CallCmd(int line, int col, String id, List<Expr> args) {
        super(line, col);
        this.id = id;
        this.args = args;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitCallCmd(this);
    }
}
