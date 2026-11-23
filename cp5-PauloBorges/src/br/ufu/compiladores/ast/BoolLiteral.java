package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class BoolLiteral extends Expr {

    public final boolean value;
    public final String lexeme;

    public BoolLiteral(int line, int col, boolean value, String lexeme) {
        super(line, col);
        this.value = value;
        this.lexeme = lexeme;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitBoolLiteral(this);
    }
}
