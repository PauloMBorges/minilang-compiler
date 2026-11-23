package br.ufu.compiladores.ast;

/* Categoria sintatica: expressoes. */
public abstract class Expr extends Node {

    protected Expr(int line, int col) {
        super(line, col);
    }
}
