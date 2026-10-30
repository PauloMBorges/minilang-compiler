package br.ufu.compiladores.ast;

import br.ufu.compiladores.visitor.Visitor;

public class IntLiteral extends Expr {

    public final long value;
    public final String lexeme;

    public IntLiteral(int line, int col, long value, String lexeme) {
        super(line, col);
        this.value = value;
        this.lexeme = lexeme;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visitIntLiteral(this);
    }
}
