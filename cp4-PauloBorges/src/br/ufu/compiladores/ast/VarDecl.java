package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class VarDecl extends Decl {

    public final boolean isConst;
    public final Type type;
    public final String id;
    public final Expr init;   // null quando nao ha inicializacao

    public VarDecl(int line, int col, boolean isConst, Type type, String id, Expr init) {
        super(line, col);
        this.isConst = isConst;
        this.type = type;
        this.id = id;
        this.init = init;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitVarDecl(this);
    }
}
