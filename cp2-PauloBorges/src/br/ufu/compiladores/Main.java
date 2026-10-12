package br.ufu.compiladores;

import br.ufu.compiladores.lexer.LexicalError;
import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;
import br.ufu.compiladores.parser.Parser;
import br.ufu.compiladores.parser.SyntaxError;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ponto de entrada.
 * Uso:
 *   java ... Main <arquivo-fonte>            analise sintatica (CP2): ACEITO / REJEITADO + erros
 *   java ... Main --tokens <arquivo-fonte>   lista de tokens (comportamento do CP1)
 */

public class Main {
    public static void main(String[] args) throws Exception {
        boolean soTokens = args.length >= 1 && args[0].equals("--tokens");
        String arquivo = soTokens ? (args.length >= 2 ? args[1] : null)
                                  : (args.length >= 1 ? args[0] : null);
        if (arquivo == null) {
            System.err.println("Uso: Main [--tokens] <arquivo-fonte>");
            System.exit(1);
        }

        String source = Files.readString(Path.of(arquivo));
        Scanner scanner = new Scanner(source);

        if (soTokens) {
            imprimirTokens(scanner);
        } else {
            analisar(scanner);
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
        Parser parser = new Parser(scanner);
        parser.parse();

        relatarErrosLexicos(scanner);

        var errosSintaticos = parser.getErrors();
        if (!errosSintaticos.isEmpty()) {
            System.err.println("\n--- " + errosSintaticos.size() + " erro(s) sintático(s) ---");
            for (SyntaxError e : errosSintaticos) {
                System.err.println(e);
            }
        }

        int total = scanner.getErrors().size() + errosSintaticos.size();
        System.out.println(total == 0 ? "ACEITO" : "REJEITADO (" + total + " erro(s))");
    }

    /* Relatório de erros léxicos (mesmo formato do CP1). */
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
