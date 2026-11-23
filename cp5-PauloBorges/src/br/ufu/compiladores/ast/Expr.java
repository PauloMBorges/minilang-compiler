package br.ufu.compiladores.ast;

/* Categoria sintatica: expressoes. */
public abstract class Expr extends Node {

    public br.ufu.compiladores.types.Type type;   // preenchido pelo verificador (CP5)

    protected Expr(int line, int col) {
        super(line, col);
    }
}