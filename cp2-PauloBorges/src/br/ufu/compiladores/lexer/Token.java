package br.ufu.compiladores.lexer;

/**
 * Um token produzido pela analise lexica.
 *
 * Carrega tres informacoes:
 *   - type:   a categoria (o que é, para o parser)
 *   - lexeme: o texto exato reconhecido no codigo-fonte
 *   - line/col: a posicao, usada para mensagens de erro
 */

public record Token(TokenType type, String lexeme, int line, int col) {

    @Override
    public String toString() {
        // Formato util para depurar: TIPO('lexema')@linha:coluna
        if (lexeme == null || lexeme.isEmpty()) {
            return type + "@" + line + ":" + col;
        }
        return type + "('" + lexeme + "')@" + line + ":" + col;
    }
}
