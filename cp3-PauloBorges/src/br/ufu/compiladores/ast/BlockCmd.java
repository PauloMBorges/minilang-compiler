package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class BlockCmd extends Cmd {

    public final List<Cmd> cmds;

    public BlockCmd(int line, int col, List<Cmd> cmds) {
        super(line, col);
        this.cmds = cmds;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBlock(this);
    }
}
