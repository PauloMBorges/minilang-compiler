package br.ufu.compiladores.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 5: as regras de tipos isoladas, sem passar pelo parser
 * (Secao 5 do enunciado).
 *
 * Cada teste cobre linhas da tabela de operadores da Parte 1: para cada
 * linha, pelo menos um caso aceito (o tipo do resultado) e um recusado (o
 * tipo de erro).
 */
public class TypesTest {

    /** + - * / entre int e int. */
    @Test void aritmeticosInt() {
        assertSame(Type.INT, Types.binary("+", Type.INT, Type.INT));
        assertSame(Type.INT, Types.binary("-", Type.INT, Type.INT));
        assertSame(Type.INT, Types.binary("*", Type.INT, Type.INT));
        assertSame(Type.INT, Types.binary("/", Type.INT, Type.INT));
        assertSame(Type.ERROR, Types.binary("+", Type.INT, Type.STRING));
    }

    /** + - * / com algum double (e o que acontece com o int). */
    @Test void aritmeticosComDouble() {
        assertSame(Type.DOUBLE, Types.binary("*", Type.INT, Type.DOUBLE));
        assertSame(Type.DOUBLE, Types.binary("/", Type.DOUBLE, Type.INT));
        assertSame(Type.DOUBLE, Types.binary("+", Type.DOUBLE, Type.DOUBLE));
        assertSame(Type.DOUBLE, Types.binary("-", Type.INT, Type.DOUBLE));
        assertSame(Type.ERROR, Types.binary("+", Type.DOUBLE, Type.STRING));
    }

    /** % (e os operandos que ele recusa). */
    @Test void modulo() {
        assertSame(Type.INT, Types.binary("%", Type.INT, Type.INT));
        assertSame(Type.ERROR, Types.binary("%", Type.DOUBLE, Type.INT));
        assertSame(Type.ERROR, Types.binary("%", Type.INT, Type.DOUBLE));
    }

    /** ** (potencia): operandos aceitos e tipo do resultado. */
    @Test void potencia() {
        assertSame(Type.INT, Types.binary("**", Type.INT, Type.INT));
        assertSame(Type.DOUBLE, Types.binary("**", Type.INT, Type.DOUBLE));
        assertSame(Type.DOUBLE, Types.binary("**", Type.DOUBLE, Type.INT));
        assertSame(Type.ERROR, Types.binary("**", Type.BOOL, Type.INT));
    }

    /** string com + (concatenacao) e com os demais operadores. */
    @Test void strings() {
        assertSame(Type.STRING, Types.binary("+", Type.STRING, Type.STRING));
        assertSame(Type.ERROR, Types.binary("+", Type.STRING, Type.INT));
        assertSame(Type.ERROR, Types.binary("-", Type.STRING, Type.STRING));
    }

    /** char com char e char com int, conforme a decisao da Parte 1. */
    @Test void charEInt() {
        assertSame(Type.BOOL, Types.binary("<", Type.CHAR, Type.CHAR));
        assertSame(Type.BOOL, Types.binary(">=", Type.CHAR, Type.CHAR));
        assertSame(Type.ERROR, Types.binary("+", Type.CHAR, Type.INT));
        assertSame(Type.ERROR, Types.binary("<", Type.CHAR, Type.INT));
    }

    /** bool em aritmetica: recusado conforme a Parte 1. */
    @Test void boolEmAritmetica() {
        assertSame(Type.ERROR, Types.binary("+", Type.BOOL, Type.INT));
        assertSame(Type.ERROR, Types.binary("*", Type.INT, Type.BOOL));
        assertSame(Type.ERROR, Types.binary("+", Type.BOOL, Type.BOOL));
    }

    /** < <= > >= : o resultado e bool; pares aceitos e recusados. */
    @Test void relacionais() {
        assertSame(Type.BOOL, Types.binary("<", Type.INT, Type.DOUBLE));
        assertSame(Type.BOOL, Types.binary(">=", Type.DOUBLE, Type.INT));
        assertSame(Type.BOOL, Types.binary("<=", Type.INT, Type.INT));
        assertSame(Type.ERROR, Types.binary("<", Type.STRING, Type.STRING));
        assertSame(Type.ERROR, Types.binary("<", Type.BOOL, Type.BOOL));
    }

    /** == != : mesmo tipo e os pares mistos aceitos pela Parte 1. */
    @Test void igualdade() {
        assertSame(Type.BOOL, Types.binary("==", Type.STRING, Type.STRING));
        assertSame(Type.BOOL, Types.binary("==", Type.INT, Type.DOUBLE));
        assertSame(Type.BOOL, Types.binary("!=", Type.DOUBLE, Type.INT));
        assertSame(Type.BOOL, Types.binary("==", Type.BOOL, Type.BOOL));
        assertSame(Type.ERROR, Types.binary("==", Type.CHAR, Type.INT));
        assertSame(Type.ERROR, Types.binary("==", Type.BOOL, Type.INT));
    }

    /** && || entre bool, e o que acontece com outros tipos. */
    @Test void logicos() {
        assertSame(Type.BOOL, Types.binary("&&", Type.BOOL, Type.BOOL));
        assertSame(Type.BOOL, Types.binary("||", Type.BOOL, Type.BOOL));
        assertSame(Type.ERROR, Types.binary("||", Type.INT, Type.BOOL));
        assertSame(Type.ERROR, Types.binary("&&", Type.INT, Type.INT));
    }

    /** unarios: - (numerico) e ! (bool). */
    @Test void unarios() {
        assertSame(Type.INT, Types.unary("-", Type.INT));
        assertSame(Type.DOUBLE, Types.unary("-", Type.DOUBLE));
        assertSame(Type.ERROR, Types.unary("-", Type.STRING));
        assertSame(Type.BOOL, Types.unary("!", Type.BOOL));
        assertSame(Type.ERROR, Types.unary("!", Type.INT));
    }

    /** assignable: a diagonal e as conversoes implicitas (e as recusadas). */
    @Test void compatibilidade() {
        assertTrue(Types.assignable(Type.DOUBLE, Type.INT));
        assertFalse(Types.assignable(Type.INT, Type.DOUBLE));

        assertTrue(Types.assignable(Type.INT, Type.INT));
        assertTrue(Types.assignable(Type.DOUBLE, Type.DOUBLE));
        assertTrue(Types.assignable(Type.BOOL, Type.BOOL));
        assertTrue(Types.assignable(Type.CHAR, Type.CHAR));
        assertTrue(Types.assignable(Type.STRING, Type.STRING));

        assertFalse(Types.assignable(Type.BOOL, Type.INT));
    }

    /** sem cascata: um operando de erro da erro no resultado, e erro e compativel com tudo. */
    @Test void erroSobeSemCascata() {
        assertSame(Type.ERROR, Types.binary("+", Type.ERROR, Type.INT));
        assertSame(Type.ERROR, Types.binary("==", Type.INT, Type.ERROR));
        assertSame(Type.ERROR, Types.unary("-", Type.ERROR));
        assertTrue(Types.assignable(Type.INT, Type.ERROR));
        assertTrue(Types.assignable(Type.ERROR, Type.STRING));
    }
}