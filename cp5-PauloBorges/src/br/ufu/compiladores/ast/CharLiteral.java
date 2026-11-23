package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class CharLiteral extends Expr {

    public final char value;
    public final String lexeme;

    public CharLiteral(int line, int col, char value, String lexeme) {
        super(line, col);
        this.value = value;
        this.lexeme = lexeme;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitCharLiteral(this);
    }
}
