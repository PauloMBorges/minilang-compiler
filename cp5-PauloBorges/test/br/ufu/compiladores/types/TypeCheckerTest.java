package br.ufu.compiladores.types;

import br.ufu.compiladores.ast.Program;
import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.parser.Parser;
import br.ufu.compiladores.symbols.SemanticError;
import br.ufu.compiladores.visitor.IdentificationVisitor;
import br.ufu.compiladores.visitor.TypeChecker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 5: o verificador sobre programas inteiros (Secao 5 do
 * enunciado).
 *
 * Cada programa test/programas/<nome>.txt tem a saida esperada em
 * test/esperado/<nome>.txt, escrita a mao a partir das regras da Parte 1
 * (tipos-a-mao.pdf), ANTES de rodar o verificador.
 *
 * Saida comparada (saidaDe): os erros da identificacao (CP4), os erros de
 * tipo e os avisos, um por linha, ordenados pela linha do programa (a ordem
 * de cada passada e mantida para a mesma linha).
 *
 */
public class TypeCheckerTest {

    private static final Path PROGRAMAS = Path.of("test", "programas");
    private static final Path ESPERADO = Path.of("test", "esperado");

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------

    /** Roda a acao com limite de tempo: se o verificador travar, o teste falha. */
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

    /** AST de um programa sintaticamente valido (as passadas so rodam sobre AST valida). */
    private static Program arvoreDe(String fonte) {
        Scanner scanner = new Scanner(fonte);
        Parser.ParseResult result = new Parser(scanner).parse();
        assertTrue(scanner.getErrors().isEmpty(), "erros lexicos: " + scanner.getErrors());
        assertTrue(result.errors().isEmpty(), "erros sintaticos: " + result.errors());
        assertNotNull(result.ast(), "a AST veio nula para um programa valido");
        return result.ast();
    }

    /** Identificacao + verificador; devolve todas as mensagens, ordenadas por linha. */
    private static String saidaDe(String arquivo) throws IOException {
        String texto = fonte(arquivo);
        return comTimeout(arquivo, () -> {
            Program ast = arvoreDe(texto);
            IdentificationVisitor identificacao = new IdentificationVisitor();
            ast.accept(identificacao);
            TypeChecker checker = new TypeChecker();
            ast.accept(checker);

            List<SemanticError> todas = new ArrayList<>(identificacao.getErrors());
            todas.addAll(checker.getErrors());
            todas.addAll(checker.getWarnings());
            todas.sort(Comparator.comparingInt(SemanticError::line));  // estavel
            return todas.stream().map(SemanticError::toString).collect(Collectors.joining("\n"));
        });
    }

    /** Compara a saida de test/programas/<nome>.txt com test/esperado/<nome>.txt. */
    private static void confere(String nome) throws IOException {
        assertEquals(esperado(nome + ".txt"), normalizar(saidaDe(nome + ".txt")));
    }

    // ------------------------------------------------------------------
    //  PROGRAMAS OBRIGATORIOS
    // ------------------------------------------------------------------

    /** valido com todos os tipos e comandos: nenhuma mensagem. */
    @Test void validoCompleto() throws IOException { confere("valido-completo"); }

    @Test void erroEmExpressao() throws IOException { confere("erro-expressao"); }

    @Test void condicaoNaoBooleana() throws IOException { confere("cond_int"); }

    @Test void returnIncompativel() throws IOException { confere("return-incompativel"); }

    /** aridade e tipo de argumento. */
    @Test void chamadaErrada() throws IOException { confere("chamada-errada"); }

    @Test void procedimentoComoExpressao() throws IOException { confere("proc-como-expressao"); }

    /** varios erros numa rodada, em ordem, sem cascata. */
    @Test void variosErrosSemCascata() throws IOException { confere("varios-erros"); }

    // ------------------------------------------------------------------
    //  FUNCOES
    // ------------------------------------------------------------------

    @Test void funcaoRecursiva() throws IOException { confere("funcao-recursiva"); }

    /** mutuamente recursiva: mostra a decisao 7 (permitida ou nao). */
    @Test void recursaoMutua() throws IOException { confere("recursao-mutua"); }

    /** a MiniLang nao tem funcoes aninhadas (so globais): o caso fica documentado no README. */

    // ------------------------------------------------------------------
    //  VARIAVEIS SEM VALOR (avisos)
    // ------------------------------------------------------------------

    @Test void variaveisSemValor() throws IOException { confere("sem-valor"); }

    // ------------------------------------------------------------------
    //  UM PROGRAMA POR DECISAO DE PROJETO (Parte 1)
    // ------------------------------------------------------------------

    @Test void decisaoIntDouble() throws IOException { confere("decisao-int-double"); }

    @Test void decisaoCharInt() throws IOException { confere("decisao-char-int"); }

    @Test void decisaoBoolAritmetica() throws IOException { confere("decisao-bool-aritmetica"); }

    @Test void decisaoString() throws IOException { confere("decisao-string"); }

    @Test void decisaoIgualdadeMista() throws IOException { confere("decisao-igualdade-mista"); }

    @Test void decisaoFuncaoComoComando() throws IOException { confere("decisao-funcao-comando"); }

    /** decisao 7 (recursao mutua): o programa e o recursao-mutua.txt, da secao FUNCOES. */

    @Test void decisaoArgumentos() throws IOException { confere("decisao-argumentos"); }

    @Test void decisaoReturnCaminhos() throws IOException { confere("decisao-return-caminhos"); }

    @Test void decisaoSobrecarga() throws IOException { confere("decisao-sobrecarga"); }

    @Test void decisaoFuncaoSemParenteses() throws IOException { confere("decisao-funcao-sem-parenteses"); }

    // ------------------------------------------------------------------
    //  ROBUSTEZ: nenhuma entrada lanca excecao ou trava
    // ------------------------------------------------------------------

    @Test void verificadorNuncaLancaExcecao() throws IOException {
        String[] programas = {
            "valido-completo", "erro-expressao", "cond_int", "return-incompativel",
            "chamada-errada", "proc-como-expressao", "varios-erros", "funcao-recursiva",
            "sem-valor", "decisao-int-double", "decisao-char-int", "decisao-bool-aritmetica",
            "decisao-string", "decisao-igualdade-mista", "decisao-funcao-comando",
            "recursao-mutua", "decisao-argumentos", "decisao-return-caminhos",
            "decisao-sobrecarga", "decisao-funcao-sem-parenteses"
        };
        for (String nome : programas) {
            assertDoesNotThrow(() -> saidaDe(nome + ".txt"), "excecao no verificador em " + nome);
        }
        // os programas do CP3 e do CP4 tambem passam pelo verificador sem excecao
        String[] anteriores = {
            "validos/valido01.ml", "validos/valido02.ml", "validos/valido03.ml",
            "escopo/sombreamento.ml", "escopo/redeclaracao.ml", "escopo/nao-declarado.ml",
            "escopo/tres-erros.ml", "escopo/procedimento-recursivo.ml", "escopo/uso-apos-bloco.ml",
            "escopo/decisao-sombreamento.ml", "escopo/decisao-declarar-antes.ml",
            "escopo/decisao-recursao-mutua.ml", "escopo/decisao-parametros-corpo.ml",
            "escopo/decisao-espaco-nomes.ml"
        };
        for (String arquivo : anteriores) {
            assertDoesNotThrow(() -> saidaDe(arquivo), "excecao no verificador em " + arquivo);
        }
    }
}
