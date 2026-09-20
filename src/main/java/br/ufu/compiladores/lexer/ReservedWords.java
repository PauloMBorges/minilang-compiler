package br.ufu.compiladores.lexer;

import java.util.Map;

/* Tabela de palavras reservadas. */
public final class ReservedWords {

    private static final Map<String, TokenType> TABLE = Map.ofEntries(
        Map.entry("int",    TokenType.INT),
        Map.entry("bool",   TokenType.BOOL),
        Map.entry("double", TokenType.DOUBLE),
        Map.entry("true",   TokenType.TRUE),
        Map.entry("false",  TokenType.FALSE),
        Map.entry("if",     TokenType.IF),
        Map.entry("elif",   TokenType.ELIF),
        Map.entry("else",   TokenType.ELSE),
        Map.entry("while",  TokenType.WHILE),
        Map.entry("return", TokenType.RETURN),
        Map.entry("const",  TokenType.CONST),
        Map.entry("print",  TokenType.PRINT),
        Map.entry("read",   TokenType.READ)
    );

    private ReservedWords() {}

    /**
     * @param lexeme texto ja reconhecido como identificador
     * @return o TokenType da reservada, ou ID se o lexema nao for reservado.
     */
    public static TokenType classify(String lexeme) {
        return TABLE.getOrDefault(lexeme, TokenType.ID);
    }
}
