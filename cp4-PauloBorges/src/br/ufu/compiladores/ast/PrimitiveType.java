package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class PrimitiveType extends Type {

    public final String name;

    public PrimitiveType(int line, int col, String name) {
        super(line, col);
        this.name = name;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitPrimitiveType(this);
    }
}
