package br.ufu.compiladores.ast;

import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.visitor.Visitor;

/*
 * Raiz da hierarquia de nos da AST (Checkpoint 3, Aula 7).
 *
 * Todo no guarda a posicao (linha e coluna) do primeiro token que o originou:
 * a analise semantica vai usar essa posicao para reportar erros.
 *
 * accept() e o unico acrescimo que o padrao Visitor faz a hierarquia. Cada no
 * concreto implementa accept chamando o metodo do visitor com o seu nome, por
 * exemplo: return v.visitBin(this);
 *
 * As categorias (classes abstratas) e os nos concretos saem do projeto feito
 * no papel e registrado em ast-design.md.
 */
public abstract class Node {

    private final int line;
    private final int col;

    protected Node(int line, int col) {
        this.line = line;
        this.col = col;
    }

    /** Posicao tirada do primeiro token do no. */
    protected Node(Token first) {
        this(first.line(), first.col());
    }

    public int line() {
        return line;
    }

    public int col() {
        return col;
    }

    public abstract <T> T accept(Visitor<T> v);
}
