package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class DeclCmd extends Cmd {

    public final VarDecl decl;

    public DeclCmd(int line, int col, VarDecl decl) {
        super(line, col);
        this.decl = decl;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitDeclCmd(this);
    }
}
