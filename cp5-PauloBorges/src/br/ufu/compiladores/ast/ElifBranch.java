package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class ElifBranch extends Node {

    public final Expr cond;
    public final BlockCmd block;

    public ElifBranch(int line, int col, Expr cond, BlockCmd block) {
        super(line, col);
        this.cond = cond;
        this.block = block;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitElif(this);
    }
}
