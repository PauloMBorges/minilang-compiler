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
 * Cada teste esta @Disabled ate o programa correspondente ser escrito e o
 * parser implementar as construcoes que ele usa: remova o @Disabled e
 * complete os asserts (TODO) a medida que avancar.
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
    //  PROGRAMAS VALIDOS (minimo 3): tipos, controle, procedimentos/funcoes
    // ------------------------------------------------------------------

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void valido01() {
        Parser p = analisar("validos/valido01.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void valido02() {
        Parser p = analisar("validos/valido02.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void valido03() {
        Parser p = analisar("validos/valido03.ml");
        assertTrue(p.getErrors().isEmpty(), listar(p));
    }

    // ------------------------------------------------------------------
    //  PROGRAMAS INVALIDOS (minimo 3, cada um com um erro sintatico diferente)
    // ------------------------------------------------------------------

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void invalido01() {
        Parser p = analisar("invalidos/invalido01.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        // TODO: conferir a linha do erro, ex.: assertEquals(3, p.getErrors().get(0).line());
    }

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void invalido02() {
        Parser p = analisar("invalidos/invalido02.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        // TODO: conferir a linha do erro
    }

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void invalido03() {
        Parser p = analisar("invalidos/invalido03.ml");
        assertFalse(p.getErrors().isEmpty(), "o erro nao foi detectado");
        // TODO: conferir a linha do erro
    }

    // ------------------------------------------------------------------
    //  MULTIPLOS ERROS NUMA RODADA (modo panico)
    // ------------------------------------------------------------------

    @Disabled("TODO: escrever o programa e implementar o parser")
    @Test void multiplosErros() {
        Parser p = analisar("invalidos/multiplos-erros.ml");
        assertTrue(p.getErrors().size() >= 2, listar(p));
        // TODO: conferir as linhas de cada erro esperado
    }
}
