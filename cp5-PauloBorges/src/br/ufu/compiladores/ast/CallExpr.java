package br.ufu.compiladores.ast;

import java.util.List;

import br.ufu.compiladores.visitor.Visitor;
import br.ufu.compiladores.symbols.Symbol;

public class CallExpr extends Expr {

    public final String id;
    public final List<Expr> args;
    public Symbol decl; // preenchido pela passada de identificação; null se não declarado

    public CallExpr(int line, int col, String id, List<Expr> args) {
        super(line, col);
        this.id = id;
        this.args = args;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitCallExpr(this);
    }
}
