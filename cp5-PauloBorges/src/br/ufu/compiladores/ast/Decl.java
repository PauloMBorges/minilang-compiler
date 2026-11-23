package br.ufu.compiladores.ast;

/* Categoria sintatica: declaracoes. */
public abstract class Decl extends Node {

    protected Decl(int line, int col) {
        super(line, col);
    }
}
