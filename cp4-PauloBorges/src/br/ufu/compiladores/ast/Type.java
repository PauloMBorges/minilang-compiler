package br.ufu.compiladores.ast;

/* Categoria sintatica: tipos. */
public abstract class Type extends Node {

    protected Type(int line, int col) {
        super(line, col);
    }
}
