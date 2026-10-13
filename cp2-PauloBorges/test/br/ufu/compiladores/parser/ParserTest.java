package br.ufu.compiladores.parser;

import br.ufu.compiladores.lexer.Scanner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 2 (secao 3.3 do enunciado).
 *
 * Os programas de teste ficam em test/programas/ (validos/ e invalidos/).
 */

public class ParserTest {

    private static final Path PROGRAMAS = Path.of("test", "programas");

    /** Analisa um arquivo de test/programas/ com limite de tempo: se o parser travar, o teste falha. */
    private Parser analisar(String arquivo) {
        return assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            String source = Files.readString(PROGRAMAS.resolve(arquivo));
            Parser parser = new Parser(new Scanner(source));
            parser.parse();
            return parser;
        }, "o parser travou (loop infinito?) em " + arquivo);
    }

    /** Mensagem util quando um programa valido e rejeitado: lista os erros encontrados. */
    private static String listar(Parser p) {
        return String.join("\n", p.getErrors().stream().map(SyntaxError::toString).toList());
    }

    // ------------------------------------------------------------------
    //  PROGRAMAS VALIDOS: tipos, controle, procedimentos/funcoes
    // ------------------------------------------------------------------

    @Test void valido01() {
        Parser p = analisar("validos/valido01.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    @Test void valido02() {
        Parser p = analisar("validos/valido02.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    @Test void valido03() {
        Parser p = analisar("validos/valido03.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    // ------------------------------------------------------------------
    //  PROGRAMAS INVALIDOS 
    // ------------------------------------------------------------------

    @Test void invalido01() {
        Parser p = analisar("invalidos/invalido01.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        assertEquals(3, p.getErrors().get(0).line(), "Erro esperado na linha 3 (falta de ;)");
    }

    @Test void invalido02() {
        Parser p = analisar("invalidos/invalido02.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        assertEquals(2, p.getErrors().get(0).line(), "Erro esperado na linha 2 (parentese/chave desbalanceada)");
    }

    @Test void invalido03() {
        Parser p = analisar("invalidos/invalido03.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        assertEquals(3, p.getErrors().get(0).line(), "Erro esperado na linha 3 (token invalido em comando)");
    }

    // ------------------------------------------------------------------
    //  MULTIPLOS ERROS NUMA RODADA (modo panico)
    // ------------------------------------------------------------------

    @Test void multiplosErros() {
        Parser p = analisar("invalidos/multiplos-erros.ml");
        assertTrue(p.getErrors().size() >= 3, listar(p));
        
        assertEquals(2, p.getErrors().get(0).line(), "Primeiro erro na linha 2");
        assertEquals(4, p.getErrors().get(1).line(), "Segundo erro na linha 4");
        assertEquals(10, p.getErrors().get(2).line(), "Terceiro erro na linha 10");
    }
}
