package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class IfCmd extends Cmd {

    public final Expr cond;
    public final BlockCmd thenCmd;
    public final List<ElifBranch> elifs;
    public final BlockCmd elseCmd;   // null quando nao ha else

    public IfCmd(int line, int col, Expr cond, BlockCmd thenCmd, List<ElifBranch> elifs, BlockCmd elseCmd) {
        super(line, col);
        this.cond = cond;
        this.thenCmd = thenCmd;
        this.elifs = elifs;
        this.elseCmd = elseCmd;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitIf(this);
    }
}
