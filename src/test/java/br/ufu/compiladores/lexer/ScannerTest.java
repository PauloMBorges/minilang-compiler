package br.ufu.compiladores.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste do scanner.
 *
 * Teste de cada token isoladamente antes de linhas inteiras,
 * com casos de erro. Abaixo ficam esqueletos por categoria — preencha
 * conforme for implementando cada AFD. Descomente ao implementar.
 */

public class ScannerTest {

    // Helper: pega o primeiro token de uma fonte.
    private Token first(String src) {
        return new Scanner(src).nextToken();
    }

    // --- Identificadores e reservadas ---
    @Test
    void identificadorSimples() {
        // assertEquals(TokenType.ID, first("taxa_juros").type());
    }

    @Test
    void palavraReservada() {
        // assertEquals(TokenType.WHILE, first("while").type());
    }

    // --- Literais numericos ---
    @Test
    void inteiroComSeparador() {
        // assertEquals(TokenType.INT_LIT, first("1_000_000").type());
    }

    @Test
    void doubleComCientifica() {
        // assertEquals(TokenType.DOUBLE_LIT, first("6.02e23").type());
    }

    // --- Operadores com lookahead ---
    @Test
    void starVsPow() {
        // assertEquals(TokenType.STAR, first("*").type());
        // assertEquals(TokenType.POW,  first("**").type());
    }

    // --- Casos de ERRO (obrigatorios) ---
    @Test
    void stringSemFecharEhErro() {
        // assertThrows(LexicalError.class, () -> first("\"sem fim"));
    }

    @Test
    void caractereInesperadoEhErro() {
        // assertThrows(LexicalError.class, () -> first("@"));
    }
}
