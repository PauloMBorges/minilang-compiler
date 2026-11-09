package br.ufu.compiladores;

import br.ufu.compiladores.ast.Program;
import br.ufu.compiladores.lexer.LexicalError;
import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;
import br.ufu.compiladores.parser.Parser;
import br.ufu.compiladores.parser.SyntaxError;
import br.ufu.compiladores.visitor.AstPrinter;
import br.ufu.compiladores.visitor.AstUnparser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Ponto de entrada.
 * Uso:
 *   java ... Main <arquivo-fonte>                analise sintatica (CP2): ACEITO / REJEITADO + erros
 *   java ... Main --tokens <arquivo-fonte>       lista de tokens (comportamento do CP1)
 *   java ... Main --arvore <arquivo-fonte>       imprime a AST (CP3, impressor de arvore)
 *   java ... Main --reimprimir <arquivo-fonte>   reimprime o codigo-fonte a partir da AST (CP3)
 *
 * Nos modos --arvore e --reimprimir, um programa com erro nao tem arvore:
 * os erros sao relatados e nada e impresso na saida padrao alem do veredito.
 */
public class Main {

    private static final String USO =
            "Uso: Main [--tokens | --arvore | --reimprimir] <arquivo-fonte>";

    public static void main(String[] args) throws Exception {
        String modo = "";
        String arquivo = null;
        if (args.length == 1 && !args[0].startsWith("--")) {
            arquivo = args[0];
        } else if (args.length == 2) {
            modo = args[0];
            arquivo = args[1];
        }
        if (arquivo == null) {
            System.err.println(USO);
            System.exit(1);
        }

        String source = Files.readString(Path.of(arquivo));
        Scanner scanner = new Scanner(source);

        switch (modo) {
            case ""             -> analisar(scanner);
            case "--tokens"     -> imprimirTokens(scanner);
            case "--arvore"     -> imprimirArvore(scanner);
            case "--reimprimir" -> reimprimir(scanner);
            default -> {
                System.err.println("Modo desconhecido: " + modo);
                System.err.println(USO);
                System.exit(1);
            }
        }
    }

    /* CP1: imprime a lista de tokens. */
    private static void imprimirTokens(Scanner scanner) {
        Token t;
        do {
            t = scanner.nextToken();
            System.out.println(t);
        } while (t.type() != TokenType.EOF);
        relatarErrosLexicos(scanner);
    }

    /* CP2: roda o parser e informa se o programa foi aceito. */
    private static void analisar(Scanner scanner) {
        Parser.ParseResult result = new Parser(scanner).parse();
        relatarErros(scanner, result);
    }

    /* CP3: imprime a AST com o impressor de arvore. */
    private static void imprimirArvore(Scanner scanner) {
        Program ast = arvoreOuNull(scanner);
        if (ast == null) return;
        AstPrinter printer = new AstPrinter();
        ast.accept(printer);
        System.out.print(printer.getOutput());
    }

    /* CP3: reimprime o codigo-fonte na forma canonica a partir da AST. */
    private static void reimprimir(Scanner scanner) {
        Program ast = arvoreOuNull(scanner);
        if (ast == null) return;
        AstUnparser unparser = new AstUnparser();
        ast.accept(unparser);
        System.out.print(unparser.getOutput());
    }

    /*
     * Roda o parser. Devolve a AST se o programa nao tem erro lexico nem
     * sintatico; caso contrario, relata os erros e devolve null.
     */
    private static Program arvoreOuNull(Scanner scanner) {
        Parser.ParseResult result = new Parser(scanner).parse();
        if (result.ast() == null || !scanner.getErrors().isEmpty()) {
            relatarErros(scanner, result);
            return null;
        }
        return result.ast();
    }

    /* Relatorio de erros lexicos e sintaticos + veredito (formato do CP2). */
    private static void relatarErros(Scanner scanner, Parser.ParseResult result) {
        relatarErrosLexicos(scanner);

        List<SyntaxError> errosSintaticos = result.errors();
        if (!errosSintaticos.isEmpty()) {
            System.err.println("\n--- " + errosSintaticos.size() + " erro(s) sintático(s) ---");
            for (SyntaxError e : errosSintaticos) {
                System.err.println(e);
            }
        }

        int total = scanner.getErrors().size() + errosSintaticos.size();
        System.out.println(total == 0 ? "ACEITO" : "REJEITADO (" + total + " erro(s))");
    }

    /* Relatorio de erros lexicos (mesmo formato do CP1). */
    private static void relatarErrosLexicos(Scanner scanner) {
        var errors = scanner.getErrors();
        if (!errors.isEmpty()) {
            System.err.println("\n--- " + errors.size() + " erro(s) léxico(s) ---");
            for (LexicalError e : errors) {
                System.err.println(e.getMessage());
            }
        }
    }
}