package br.ufu.compiladores.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    private Token first(String src) {
        return new Scanner(src).nextToken();
    }

    // --- identificadores e reservadas ---
    @Test void identificador()      { assertEquals(TokenType.ID,    first("taxa_juros").type()); }
    @Test void reservadaWhile()     { assertEquals(TokenType.WHILE, first("while").type()); }
    @Test void idComComecoUnder()   { assertEquals(TokenType.ID,    first("_tmp").type()); }

    // --- números ---
    @Test void inteiroSeparador()   { assertEquals(TokenType.INT_LIT,    first("1_000_000").type()); }
    @Test void doubleCientifica()   { assertEquals(TokenType.DOUBLE_LIT, first("6.02e23").type()); }
    @Test void doublePonto()        { assertEquals(TokenType.DOUBLE_LIT, first("3.14").type()); }
    @Test void expoenteComSinal()   { assertEquals(TokenType.DOUBLE_LIT, first("1e-10").type()); }

    // --- operadores: lookahead ---
    @Test void star()   { assertEquals(TokenType.STAR,  first("*").type()); }
    @Test void pow()    { assertEquals(TokenType.POW,   first("**").type()); }
    @Test void assign() { assertEquals(TokenType.ASSIGN,first("=").type()); }
    @Test void eqEq()   { assertEquals(TokenType.EQ_EQ, first("==").type()); }
    @Test void and()    { assertEquals(TokenType.AND,   first("&&").type()); }

    // --- casos de ERRO ---
    @Test void pontoFinalSemDigito() {
        assertThrows(LexicalError.class, () -> first("3."));
    }
    @Test void expoenteSemDigito() {
        assertThrows(LexicalError.class, () -> first("1e"));
    }
    @Test void stringSemFecharQuebraLinha() {
        assertThrows(LexicalError.class, () -> first("\"sem fim\n"));
    }
    @Test void stringSemFecharEOF() {
        assertThrows(LexicalError.class, () -> first("\"sem fim"));
    }
    @Test void eIsoladoEhErro() {
        assertThrows(LexicalError.class, () -> first("&"));
    }
    @Test void caractereInesperado() {
        assertThrows(LexicalError.class, () -> first("@"));
    }

    // --- linha inteira: sequência de tipos ---
    @Test void linhaInteira() {
        Scanner s = new Scanner("total = total + 1;");
        TokenType[] esperado = {
            TokenType.ID, TokenType.ASSIGN, TokenType.ID,
            TokenType.PLUS, TokenType.INT_LIT, TokenType.SEMI, TokenType.EOF
        };
        for (TokenType t : esperado) {
            assertEquals(t, s.nextToken().type());
        }
    }
}