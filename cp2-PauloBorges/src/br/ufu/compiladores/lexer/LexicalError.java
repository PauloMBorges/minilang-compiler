package br.ufu.compiladores.lexer;

/* Erro lexico: caractere inesperado, string sem fechar, literal mal formado etc. */

public class LexicalError extends RuntimeException {

    private final int line;
    private final int col;

    public LexicalError(String message, int line, int col) {
        super("Erro lexico (linha " + line + ", coluna " + col + "): " + message);
        this.line = line;
        this.col = col;
    }

    public int line() { return line; }
    public int col()  { return col; }
}
