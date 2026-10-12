package br.ufu.compiladores.parser;

import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * Parser recursivo descendente (reconhecedor) da MiniLang - Checkpoint 2.
 *
 * Estrutura da Aula 6:
 *   - campos: o scanner do CP1 e o token atual (lookahead)        -> slide 4
 *   - match(): consome um terminal ou reporta erro                -> slide 5
 *   - um metodo parseX() por nao-terminal da gramatica            -> slides 6-10
 *   - error() + sincronizar(): recuperacao em modo panico          -> slides 13-15
 *
 * Sem AST nesta etapa: parse() so diz se o programa foi aceito,
 * e os erros ficam em getErrors().
 */
public class Parser {

    private final Scanner scanner;
    private Token lookahead;

    // erros sintaticos coletados (modo panico: a analise nao para no primeiro)
    private final List<SyntaxError> errors = new ArrayList<>();

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.lookahead = scanner.nextToken();  // primeiro token
    }

    /** Erros sintaticos encontrados (vazio = nenhum erro). */
    public List<SyntaxError> getErrors() {
        return errors;
    }

    // ------------------------------------------------------------------
    //  PONTO DE ENTRADA
    // ------------------------------------------------------------------

    /**
     * Analisa o programa inteiro.
     * @return true se nao houve erro sintatico.
     */
    public boolean parse() {
        parsePrograma();              // TODO: trocar pelo simbolo inicial da SUA gramatica
        match(TokenType.EOF);         // garante que a entrada inteira foi consumida
        return errors.isEmpty();
    }

    // ------------------------------------------------------------------
    //  INFRAESTRUTURA (Aula 6)
    // ------------------------------------------------------------------

    /** Consome o terminal esperado ou reporta erro sintatico (slide 5). */
    private void match(TokenType esperado) {
        if (lookahead.type() == esperado) {
            lookahead = scanner.nextToken();
        } else {
            error("esperava " + esperado + ", achei " + lookahead.type());
        }
    }

    /** Registra um erro na posicao do token atual (slide 15). */
    private void error(String msg) {
        errors.add(new SyntaxError(msg, lookahead.line(), lookahead.col()));
    }

    /** Modo panico: descarta tokens ate um do conjunto de sincronizacao ou EOF (slide 15). */
    private void sincronizar(Set<TokenType> sync) {
        while (!sync.contains(lookahead.type())
                && lookahead.type() != TokenType.EOF) {
            lookahead = scanner.nextToken();  // descarta o token
        }
    }

    // ------------------------------------------------------------------
    //  CONJUNTOS (FIRST/FOLLOW da gramatica-first-follow.md)
    // ------------------------------------------------------------------
    //
    //  Declare aqui os conjuntos usados nas decisoes e na sincronizacao,
    //  copiados da tabela do documento. Exemplo de formato:
    //
    //  // FOLLOW(X) = { ... }
    //  private static final Set<TokenType> FOLLOW_X = Set.of(TokenType.SEMI, TokenType.RBRACE);

    // ------------------------------------------------------------------
    //  UM METODO POR NAO-TERMINAL
    // ------------------------------------------------------------------
    //
    //  Molde da Aula 6 para um nao-terminal com alternativa vazia:
    //
    //  // A -> alfa | ε        FIRST(alfa) = {...}   FOLLOW(A) = {...}
    //  private void parseA() {
    //      if (lookahead esta em FIRST(alfa)) {
    //          ... terminal -> match(...); nao-terminal -> parseB(); ...
    //      } else if (lookahead esta em FOLLOW(A)) {
    //          // ε: nao consome nada
    //      } else {
    //          error("...");
    //          sincronizar(FOLLOW_A);
    //      }
    //  }
    //
    //  Sem alternativa vazia, o ultimo else e so o erro + sincronizar.
    //  Com uma unica producao, nao ha if: so a sequencia de simbolos.

    // TODO: simbolo inicial da gramatica (renomear se necessario)
    private void parsePrograma() {
    }
}
