package br.ufu.compiladores.ast;

/* Categoria sintatica: comandos. */
public abstract class Cmd extends Node {

    protected Cmd(int line, int col) {
        super(line, col);
    }
}
