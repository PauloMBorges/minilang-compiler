package br.ufu.compiladores.parser;

/* Erro sintatico: guarda a mensagem e a posicao (linha/coluna) do token onde foi detectado. */

public record SyntaxError(String message, int line, int col) {

    @Override
    public String toString() {
        return "Erro sintatico (linha " + line + ", coluna " + col + "): " + message;
    }
}
