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

    // --- char válido ---
    @Test void charSimples() {
        assertEquals(TokenType.CHAR_LIT, first("'a'").type());
    }
    @Test void charTipoReservado() {
        assertEquals(TokenType.CHAR, first("char").type());
    }
    @Test void stringTipoReservado() {
        assertEquals(TokenType.STRING, first("string").type());
    }

    // --- casos de ERRO ---
    private Scanner scanAll(String src) {
        Scanner s = new Scanner(src);
        while (s.nextToken().type() != TokenType.EOF) { }
        return s;
    }

    @Test void caractereForaDoAlfabeto() {
        Scanner s = scanAll("@");
        assertEquals(1, s.getErrors().size());
    }

    @Test void stringSemFecharEOF() {
        Scanner s = scanAll("\"sem fim");
        assertEquals(1, s.getErrors().size());
    }

    @Test void stringSemFecharQuebraLinha() {
        Scanner s = scanAll("\"sem fim\n");
        assertEquals(1, s.getErrors().size());
    }

    @Test void pontoFinalSemDigito() {
        Scanner s = scanAll("3.");
        assertEquals(1, s.getErrors().size());
    }

    @Test void expoenteSemDigito() {
        Scanner s = scanAll("1e");
        assertEquals(1, s.getErrors().size());
    }

    @Test void eIsoladoEhErro() {
        Scanner s = scanAll("&");
        assertEquals(1, s.getErrors().size());
    }

    // Teste de recuperação (continua após o erro)
    @Test void recuperaEContinuaAposErro() {
        // @ é erro, mas 'total' depois dele deve ser tokenizado
        Scanner s = new Scanner("@ total");
        Token t1 = s.nextToken();
        assertEquals(TokenType.ID, t1.type());
        assertEquals("total", t1.lexeme());
        assertEquals(1, s.getErrors().size()); // '@' foi registrado
    }

    // char malformado
    @Test void charVazioEhErro() {
        Scanner s = scanAll("''");
        assertEquals(1, s.getErrors().size());
    }
    @Test void charLongoEhErro() {
        Scanner s = scanAll("'ab'");
        assertEquals(1, s.getErrors().size());
    }
    @Test void charSemFecharEhErro() {
        Scanner s = scanAll("'a");
        assertEquals(1, s.getErrors().size());
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