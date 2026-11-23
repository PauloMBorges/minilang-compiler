package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;

public class Program extends Node {

    public final List<Decl> decls;

    public Program(int line, int col, List<Decl> decls) {
        super(line, col);
        this.decls = decls;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitProgram(this);
    }
}
