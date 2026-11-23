package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class FuncDecl extends Decl {

    public final Type type;
    public final String id;
    public final List<Param> params;
    public final BlockCmd body;

    public FuncDecl(int line, int col, Type type, String id, List<Param> params, BlockCmd body) {
        super(line, col);
        this.type = type;
        this.id = id;
        this.params = params;
        this.body = body;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitFuncDecl(this);
    }
}
