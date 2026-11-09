package br.ufu.compiladores.ast;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.parser.Parser;
import br.ufu.compiladores.visitor.AstPrinter;
import br.ufu.compiladores.visitor.AstUnparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 3 (secao 5 do enunciado).
 */
public class AstTest {

    private static final Path PROGRAMAS = Path.of("test", "programas");
    private static final Path ESPERADO = Path.of("test", "esperado");

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------

    /** Roda a acao com limite de tempo: se o parser travar, o teste falha. */
    private static <R> R comTimeout(String oQue, ThrowingSupplier<R> acao) {
        return assertTimeoutPreemptively(Duration.ofSeconds(2), acao,
                "travou (loop infinito?) em " + oQue);
    }

    /** Texto de um programa de test/programas/. */
    private static String fonte(String arquivo) throws IOException {
        return Files.readString(PROGRAMAS.resolve(arquivo));
    }

    /** Arvore esperada (escrita a mao) de test/esperado/, normalizada. */
    private static String esperado(String arquivo) throws IOException {
        return normalizar(Files.readString(ESPERADO.resolve(arquivo)));
    }

    /** Ignora \r\n x \n e linhas em branco no inicio e no fim. */
    private static String normalizar(String s) {
        return s.replace("\r\n", "\n").strip();
    }

    /**
     * Analisa o texto-fonte de um programa e devolve a saida do impressor de
     * arvore. Deve falhar o teste se o parser reportar erro.
     */
    private static String arvoreDe(String fonte) {
        return comTimeout("arvoreDe", () -> {
            Scanner scanner = new Scanner(fonte);
            Parser parser = new Parser(scanner);
            Parser.ParseResult result = parser.parse();

            assertTrue(result.errors().isEmpty(), "erros no programa valido: " + result.errors());
            assertNotNull(result.ast(), "A AST retornou nula para um programa valido");

            AstPrinter printer = new AstPrinter();
            result.ast().accept(printer);
            return printer.getOutput();
        });
    }

    /**
     * Saida do impressor de arvore para uma EXPRESSAO isolada (testes de
     * precedencia e de parenteses)
     */
    private static String arvoreDaExpressao(String expressao) {
        return comTimeout("arvoreDaExpressao", () -> {
            // Envolve a expressão em um programa mínimo
            String programaMinimo = "void main() { print(" + expressao + "); }";

            Scanner scanner = new Scanner(programaMinimo);
            Parser parser = new Parser(scanner);
            Parser.ParseResult result = parser.parse();

            assertTrue(result.errors().isEmpty(), "erros na expressao '" + expressao + "': " + result.errors());
            assertNotNull(result.ast(), "A AST retornou nula ao fazer parse da expressao");

            // Navega na AST: Program -> ProcDecl -> BlockCmd -> PrintCmd -> Expr
            Program prog = result.ast();
            ProcDecl mainProc = (ProcDecl) prog.decls.get(0);
            PrintCmd printCmd = (PrintCmd) mainProc.body.cmds.get(0);
            Expr exprNode = printCmd.expr;

            AstPrinter printer = new AstPrinter();
            exprNode.accept(printer);
            return printer.getOutput();
        });
    }

    /** Analisa o texto-fonte e devolve a saida do reimpressor de codigo-fonte. */
    private static String reimpressoDe(String fonte) {
        return comTimeout("reimpressoDe", () -> {
            Scanner scanner = new Scanner(fonte);
            Parser parser = new Parser(scanner);
            Parser.ParseResult result = parser.parse();

            assertTrue(result.errors().isEmpty(), "erros ao reimprimir programa valido: " + result.errors());
            assertNotNull(result.ast(), "A AST retornou nula ao reimprimir programa valido");

            AstUnparser unparser = new AstUnparser();
            result.ast().accept(unparser);
            return unparser.getOutput();
        });
    }

    // ------------------------------------------------------------------
    //  ARVORES DOS PROGRAMAS VALIDOS (esperadas escritas a mao)
    // ------------------------------------------------------------------

    @Test void arvoreValido01() throws IOException {
        assertEquals(esperado("valido01.txt"), normalizar(arvoreDe(fonte("validos/valido01.ml"))));
    }

    @Test void arvoreValido02() throws IOException {
        assertEquals(esperado("valido02.txt"), normalizar(arvoreDe(fonte("validos/valido02.ml"))));
    }

    @Test void arvoreValido03() throws IOException {
        assertEquals(esperado("valido03.txt"), normalizar(arvoreDe(fonte("validos/valido03.ml"))));
    }

    // ------------------------------------------------------------------
    //  PRECEDENCIA E ASSOCIATIVIDADE
    // ------------------------------------------------------------------

    @Test void precedenciaDiferente() throws IOException {
        assertEquals(esperado("prec-soma-mult.txt"), normalizar(arvoreDaExpressao("a + b * c")));
    }

    @Test void associatividadeEsquerda() throws IOException {
        assertEquals(esperado("assoc-sub.txt"), normalizar(arvoreDaExpressao("a - b - c")));
    }

    @Test void parentesesMudamArvore() throws IOException {
        assertEquals(esperado("parenteses-sub.txt"), normalizar(arvoreDaExpressao("a - (b - c)")));
    }

    // ------------------------------------------------------------------
    //  A AST NAO E A ARVORE DE DERIVACAO
    // ------------------------------------------------------------------

    @Test void parentesesRedundantesSomem() {
        assertEquals(normalizar(arvoreDaExpressao("a")), normalizar(arvoreDaExpressao("(((a)))")));
    }

    // ------------------------------------------------------------------
    //  IDA E VOLTA (round-trip) PARA TODOS OS PROGRAMAS VALIDOS
    // ------------------------------------------------------------------

    /** reimprime 2x: os dois textos e as duas arvores tem que ser iguais. */
    private static void idaEVolta(String arquivo) throws IOException {
        String original = fonte(arquivo);
        String r1 = reimpressoDe(original);
        String r2 = reimpressoDe(r1);
        assertEquals(r1, r2, "o segundo texto reimpresso difere do primeiro");
        assertEquals(normalizar(arvoreDe(original)), normalizar(arvoreDe(r1)), "a arvore mudou depois de reimprimir");
    }

    @Test void idaEVoltaValido01() throws IOException { idaEVolta("validos/valido01.ml"); }

    @Test void idaEVoltaValido02() throws IOException { idaEVolta("validos/valido02.ml"); }

    @Test void idaEVoltaValido03() throws IOException { idaEVolta("validos/valido03.ml"); }

    // ------------------------------------------------------------------
    //  REGRESSAO DO CP2: inválidos rejeitados, sem arvore e sem excecao
    // ------------------------------------------------------------------

    /**
     * Para cada programa invalido: parse() termina (sem excecao e sem travar),
     * reporta erro e NAO entrega uma arvore valida (secao 4 do enunciado).
     */
    @Test void invalidosSemArvoreESemExcecao() throws IOException {
        String[] invalidos = {
            "invalidos/invalido01.ml", "invalidos/invalido02.ml",
            "invalidos/invalido03.ml", "invalidos/multiplos-erros.ml"
        };
        for (String arquivo : invalidos) {
            String texto = fonte(arquivo);
            comTimeout(arquivo, () -> {
                Scanner scanner = new Scanner(texto);
                Parser parser = new Parser(scanner);
                Parser.ParseResult result = parser.parse();

                assertFalse(result.errors().isEmpty(), "Deveria ter reportado erro sintatico no arquivo: " + arquivo);
                assertNull(result.ast(), "A arvore deveria ser nula para o arquivo invalido: " + arquivo);

                return null;
            });
        }
    }
}