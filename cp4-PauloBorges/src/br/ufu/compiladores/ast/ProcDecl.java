package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class ProcDecl extends Decl {

    public final String id;
    public final List<Param> params;
    public final BlockCmd body;

    public ProcDecl(int line, int col, String id, List<Param> params, BlockCmd body) {
        super(line, col);
        this.id = id;
        this.params = params;
        this.body = body;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitProcDecl(this);
    }
}
