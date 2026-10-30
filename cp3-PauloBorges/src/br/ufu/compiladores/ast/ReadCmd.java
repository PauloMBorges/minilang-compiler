package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class ReadCmd extends Cmd {

    public final String id;

    public ReadCmd(int line, int col, String id) {
        super(line, col);
        this.id = id;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitRead(this);
    }
}
