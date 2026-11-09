package br.ufu.compiladores.symbols;

import br.ufu.compiladores.ast.Program;
import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.parser.Parser;
import br.ufu.compiladores.visitor.IdentificationVisitor;
import br.ufu.compiladores.visitor.ResolutionPrinter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 4: a passada de identificacao (Secao 5 do enunciado)
 *
 * Cada programa de test/programas/escopo/ tem a saida esperada em
 * test/esperado/<mesmo nome>.txt, escrita a partir do desenho da
 * secao 1 do escopo-design.md, antes de rodar a passada.
 *
 * Saida comparada (saidaDe): as resolucoes dos usos, uma por linha, seguidas
 * dos erros, um por linha, na ordem do texto. Formatos na secao 5 do
 * escopo-design.md.
 */
public class IdentificationTest {

    private static final Path PROGRAMAS = Path.of("test", "programas");
    private static final Path ESPERADO = Path.of("test", "esperado");

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------

    /** Roda a acao com limite de tempo: se a passada travar, o teste falha. */
    private static <R> R comTimeout(String oQue, ThrowingSupplier<R> acao) {
        return assertTimeoutPreemptively(Duration.ofSeconds(2), acao,
                "travou (loop infinito?) em " + oQue);
    }

    /** Texto de um programa de test/programas/. */
    private static String fonte(String arquivo) throws IOException {
        return Files.readString(PROGRAMAS.resolve(arquivo));
    }

    /** Saida esperada (escrita a mao) de test/esperado/, normalizada. */
    private static String esperado(String arquivo) throws IOException {
        return normalizar(Files.readString(ESPERADO.resolve(arquivo)));
    }

    /** Ignora \r\n x \n e linhas em branco no inicio e no fim. */
    private static String normalizar(String s) {
        return s.replace("\r\n", "\n").strip();
    }

    /** AST de um programa sintaticamente valido (a passada so roda sobre AST valida). */
    private static Program arvoreDe(String fonte) {
        Scanner scanner = new Scanner(fonte);
        Parser.ParseResult result = new Parser(scanner).parse();
        assertTrue(scanner.getErrors().isEmpty(), "erros lexicos: " + scanner.getErrors());
        assertTrue(result.errors().isEmpty(), "erros sintaticos: " + result.errors());
        assertNotNull(result.ast(), "a AST veio nula para um programa valido");
        return result.ast();
    }

    /** Roda a passada e devolve os erros, um por linha, na ordem em que foram reportados. */
    private static String errosDe(String fonte) {
        return comTimeout("errosDe", () -> {
            IdentificationVisitor passada = new IdentificationVisitor();
            arvoreDe(fonte).accept(passada);
            return passada.getErrors().stream()
                    .map(SemanticError::toString)
                    .collect(Collectors.joining("\n"));
        });
    }

    /**
     * Roda a passada e devolve, para cada identificador de uso, a declaracao a
     * que ele resolveu, uma linha por uso, na ordem do texto (formato da secao
     * 5 do escopo-design.md; usos nao declarados nao aparecem aqui, aparecem
     * nos erros). Le os campos decl da arvore decorada.
     */
    private static String resolucoesDe(String fonte) {
        return comTimeout("resolucoesDe", () -> {
            Program ast = arvoreDe(fonte);
            ast.accept(new IdentificationVisitor());   // decora a arvore
            ResolutionPrinter printer = new ResolutionPrinter();
            ast.accept(printer);                       // le os campos decl
            return printer.getOutput();
        });
    }

    /** Saida comparada com test/esperado/: resolucoes e, depois, erros. */
    private static String saidaDe(String arquivo) throws IOException {
        String texto = fonte(arquivo);
        String resolucoes = normalizar(resolucoesDe(texto));
        String erros = normalizar(errosDe(texto));
        if (resolucoes.isEmpty()) return erros;
        if (erros.isEmpty()) return resolucoes;
        return resolucoes + "\n" + erros;
    }

    /** Compara a saida de test/programas/escopo/<nome>.ml com test/esperado/<nome>.txt. */
    private static void confere(String nome) throws IOException {
        assertEquals(esperado(nome + ".txt"), saidaDe("escopo/" + nome + ".ml"));
    }

    // ------------------------------------------------------------------
    //  OS TRES PROGRAMAS OBRIGATORIOS
    // ------------------------------------------------------------------

    /** valido com sombreamento: nenhum erro e todo uso resolve a declaracao certa. */
    @Test void validoComSombreamento() throws IOException {
        assertEquals("", normalizar(errosDe(fonte("escopo/sombreamento.ml"))));
        confere("sombreamento");
    }

    @Test void redeclaracao() throws IOException { confere("redeclaracao"); }

    @Test void naoDeclarado() throws IOException { confere("nao-declarado"); }

    // ------------------------------------------------------------------
    //  UM PROGRAMA POR DECISAO DE PROJETO (secao 4 do escopo-design.md)
    // ------------------------------------------------------------------

    @Test void decisaoSombreamento() throws IOException { confere("decisao-sombreamento"); }

    @Test void decisaoDeclararAntesDeUsar() throws IOException { confere("decisao-declarar-antes"); }

    @Test void decisaoRecursaoMutua() throws IOException { confere("decisao-recursao-mutua"); }

    @Test void decisaoParametrosECorpo() throws IOException { confere("decisao-parametros-corpo"); }

    @Test void decisaoEspacoDeNomes() throws IOException { confere("decisao-espaco-nomes"); }

    // ------------------------------------------------------------------
    //  CASOS EXIGIDOS
    // ------------------------------------------------------------------

    /** pelo menos tres erros numa rodada, na ordem do texto. */
    @Test void tresErrosNaOrdem() throws IOException {
        assertTrue(normalizar(errosDe(fonte("escopo/tres-erros.ml"))).lines().count() >= 3,
                "esperava pelo menos tres erros");
        confere("tres-erros");
    }

    @Test void procedimentoRecursivo() throws IOException { confere("procedimento-recursivo"); }

    /** o nome interno nao pode ser visto depois do fechamento do bloco. */
    @Test void usoDepoisDoBloco() throws IOException { confere("uso-apos-bloco"); }

    // ------------------------------------------------------------------
    //  ROBUSTEZ: nenhuma entrada lanca excecao ou trava
    // ------------------------------------------------------------------

    @Test void passadaNuncaLancaExcecao() throws IOException {
        String[] programas = {
            "validos/valido01.ml", "validos/valido02.ml", "validos/valido03.ml",
            "escopo/sombreamento.ml", "escopo/redeclaracao.ml", "escopo/nao-declarado.ml",
            "escopo/decisao-sombreamento.ml", "escopo/decisao-declarar-antes.ml",
            "escopo/decisao-recursao-mutua.ml", "escopo/decisao-parametros-corpo.ml",
            "escopo/decisao-espaco-nomes.ml", "escopo/tres-erros.ml",
            "escopo/procedimento-recursivo.ml", "escopo/uso-apos-bloco.ml"
        };
        for (String arquivo : programas) {
            String texto = fonte(arquivo);
            assertDoesNotThrow(() -> errosDe(texto), "excecao na passada em " + arquivo);
        }
    }
}
