package br.ufu.compiladores.types;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 5: as regras de tipos isoladas, sem passar pelo parser
 * (Secao 5 do enunciado).
 *
 * Cada teste cobre linhas da tabela de operadores da Parte 1: para cada
 * linha, pelo menos um caso aceito (o tipo do resultado) e um recusado (o
 * tipo de erro). Tudo desativado (@Disabled) ate Type e Types existirem.
 */
public class TypesTest {

    /** + - * / entre int e int. */
    @Disabled("TODO") @Test void aritmeticosInt() {
        // TODO
    }

    /** + - * / com algum double (e o que acontece com o int). */
    @Disabled("TODO") @Test void aritmeticosComDouble() {
        // TODO
    }

    /** % (e os operandos que ele recusa). */
    @Disabled("TODO") @Test void modulo() {
        // TODO
    }

    /** ** (potencia): operandos aceitos e tipo do resultado. */
    @Disabled("TODO") @Test void potencia() {
        // TODO
    }

    /** string com + (concatenacao, se a Parte 1 permitir) e com os demais operadores. */
    @Disabled("TODO") @Test void strings() {
        // TODO
    }

    /** char com char e char com int, conforme a decisao da Parte 1. */
    @Disabled("TODO") @Test void charEInt() {
        // TODO
    }

    /** bool em aritmetica: recusado ou nao, conforme a Parte 1. */
    @Disabled("TODO") @Test void boolEmAritmetica() {
        // TODO
    }

    /** < <= > >= : o resultado e bool; pares aceitos e recusados. */
    @Disabled("TODO") @Test void relacionais() {
        // TODO
    }

    /** == != : mesmo tipo e os pares mistos aceitos pela Parte 1. */
    @Disabled("TODO") @Test void igualdade() {
        // TODO
    }

    /** && || entre bool, e o que acontece com outros tipos. */
    @Disabled("TODO") @Test void logicos() {
        // TODO
    }

    /** unarios: - (numerico) e ! (bool). */
    @Disabled("TODO") @Test void unarios() {
        // TODO
    }

    /** assignable: a diagonal e as conversoes implicitas (e as recusadas). */
    @Disabled("TODO") @Test void compatibilidade() {
        // TODO
    }

    /** sem cascata: um operando de erro da erro no resultado, e erro e compativel com tudo. */
    @Disabled("TODO") @Test void erroSobeSemCascata() {
        // TODO
    }
}
