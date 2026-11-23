package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;
import br.ufu.compiladores.symbols.Symbol;

public class IdExpr extends Expr {

    public final String id;
    public Symbol decl; // preenchido pela passada de identificação; null se não declarado

    public IdExpr(int line, int col, String id) {
        super(line, col);
        this.id = id;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitId(this);
    }
}
