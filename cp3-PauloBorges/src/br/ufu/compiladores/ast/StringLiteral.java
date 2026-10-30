package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class StringLiteral extends Expr {

    public final String value;
    public final String lexeme;

    public StringLiteral(int line, int col, String value, String lexeme) {
        super(line, col);
        this.value = value;
        this.lexeme = lexeme;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitStringLiteral(this);
    }
}
