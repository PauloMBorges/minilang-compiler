package br.ufu.compiladores;

import br.ufu.compiladores.lexer.LexicalError;
import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ponto de entrada: le um arquivo-fonte e imprime a lista de tokens.
 * Uso:  java ... Main examples/exemplo01.ml
 */

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Uso: Main <arquivo-fonte>");
            System.exit(1);
        }
        String source = Files.readString(Path.of(args[0]));
        Scanner scanner = new Scanner(source);

        Token t;
        do {
            t = scanner.nextToken();
            System.out.println(t);
        } while (t.type() != TokenType.EOF);

        // Relatório de erros léxicos
        var errors = scanner.getErrors();
        if (!errors.isEmpty()) {
            System.err.println("\n---" + errors.size() + " erro(s) léxico(s) ---");
            for (LexicalError e : errors) {
                System.err.println(e.getMessage());
            }
        }
    }
}
