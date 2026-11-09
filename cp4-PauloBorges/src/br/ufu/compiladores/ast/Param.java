package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class Param extends Node {

    public final Type type;
    public final String id;

    public Param(int line, int col, Type type, String id) {
        super(line, col);
        this.type = type;
        this.id = id;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitParam(this);
    }
}
