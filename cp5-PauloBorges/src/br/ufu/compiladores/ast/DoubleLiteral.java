package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class DoubleLiteral extends Expr {

    public final double value;
    public final String lexeme;

    public DoubleLiteral(int line, int col, double value, String lexeme) {
        super(line, col);
        this.value = value;
        this.lexeme = lexeme;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitDoubleLiteral(this);
    }
}
