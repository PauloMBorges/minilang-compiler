package br.ufu.compiladores.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;

/*
 * Parser recursivo descendente - Checkpoint 2.
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

    /** Erros sintaticos encontrados (vazio = nenhum erro) */
    public List<SyntaxError> getErrors() {
        return errors;
    }

    public boolean parse() {
        parsePrograma();
        match(TokenType.EOF);
        return errors.isEmpty();
    }

    /** Consome o terminal esperado ou reporta erro sintatico */
    private void match(TokenType esperado) {
        if (lookahead.type() == esperado) {
            lookahead = scanner.nextToken();
        } else {
            error("esperava " + esperado + ", achei " + lookahead.type());
        }
    }

    /** Registra um erro na posicao do token atual  */
    private void error(String msg) {
        errors.add(new SyntaxError(msg, lookahead.line(), lookahead.col()));
    }

    /** Modo panico: descarta tokens ate um do conjunto de sincronizacao ou EOF  */
    private void sincronizar(Set<TokenType> sync) {
        while (!sync.contains(lookahead.type())
                && lookahead.type() != TokenType.EOF) {
            lookahead = scanner.nextToken();  // descarta o token
        }
    }

    // ------------------------------------------------------------------
    //  CONJUNTOS (FIRST/FOLLOW da gramatica-first-follow.md)
    // ------------------------------------------------------------------

    private static final Set<TokenType> FIRST_EXPRESSAO = EnumSet.of(
            TokenType.MINUS, TokenType.NOT, TokenType.LPAREN, TokenType.ID,
            TokenType.INT_LIT, TokenType.DOUBLE_LIT, TokenType.STRING_LIT,
            TokenType.CHAR_LIT, TokenType.TRUE, TokenType.FALSE
    );

    private static final Set<TokenType> FIRST_FATOR = EnumSet.of(
            TokenType.LPAREN, TokenType.ID, TokenType.INT_LIT, TokenType.DOUBLE_LIT,
            TokenType.STRING_LIT, TokenType.CHAR_LIT, TokenType.TRUE, TokenType.FALSE
    );

    private static final Set<TokenType> FIRST_DECLARACAO = EnumSet.of(
            TokenType.CONST, TokenType.INT, TokenType.DOUBLE, TokenType.BOOL,
            TokenType.CHAR, TokenType.STRING, TokenType.VOID
    );

    private static final Set<TokenType> FIRST_COMANDO = EnumSet.of(
            TokenType.CONST, TokenType.INT, TokenType.DOUBLE, TokenType.BOOL,
            TokenType.CHAR, TokenType.STRING, TokenType.ID, TokenType.IF,
            TokenType.WHILE, TokenType.RETURN, TokenType.PRINT, TokenType.READ
    );

    private static final Set<TokenType> FIRST_TIPO = EnumSet.of(
            TokenType.INT, TokenType.DOUBLE, TokenType.BOOL, TokenType.CHAR, TokenType.STRING
    );

    // FOLLOWs usados para sincronizacao em modo panico
    private static final Set<TokenType> FOLLOW_FATOR = EnumSet.of(
            TokenType.POW, TokenType.STAR, TokenType.SLASH, TokenType.PERCENT,
            TokenType.PLUS, TokenType.MINUS, TokenType.LT, TokenType.GT,
            TokenType.LE, TokenType.GE, TokenType.EQ_EQ, TokenType.NEQ,
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_COMANDO = EnumSet.of(
            TokenType.CONST, TokenType.INT, TokenType.DOUBLE, TokenType.BOOL,
            TokenType.CHAR, TokenType.STRING, TokenType.ID, TokenType.IF,
            TokenType.WHILE, TokenType.RETURN, TokenType.PRINT, TokenType.READ,
            TokenType.RBRACE
    );

    private static final Set<TokenType> FOLLOW_LISTA_COMANDOS = EnumSet.of(TokenType.RBRACE);
    private static final Set<TokenType> FOLLOW_LISTA_DECLARACOES = EnumSet.of(TokenType.EOF);

    private static final Set<TokenType> FOLLOW_DECLARACAO = EnumSet.of(
            TokenType.CONST, TokenType.INT, TokenType.DOUBLE, TokenType.BOOL,
            TokenType.CHAR, TokenType.STRING, TokenType.VOID, TokenType.EOF
    );

    private static final Set<TokenType> FOLLOW_EXPRESSAO = EnumSet.of(
            TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    // FOLLOW de cada nivel de expressao: o do nivel de baixo = operadores do nivel de cima + FOLLOW dele
    private static final Set<TokenType> FOLLOW_EXP_AND = EnumSet.of(
            TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_EXP_IGUALDADE = EnumSet.of(
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_EXP_RELACIONAL = EnumSet.of(
            TokenType.EQ_EQ, TokenType.NEQ,
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_EXP_ADD = EnumSet.of(
            TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE,
            TokenType.EQ_EQ, TokenType.NEQ,
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_EXP_MULT = EnumSet.of(
            TokenType.PLUS, TokenType.MINUS,
            TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE,
            TokenType.EQ_EQ, TokenType.NEQ,
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    private static final Set<TokenType> FOLLOW_EXP_POT = EnumSet.of(
            TokenType.STAR, TokenType.SLASH, TokenType.PERCENT,
            TokenType.PLUS, TokenType.MINUS,
            TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE,
            TokenType.EQ_EQ, TokenType.NEQ,
            TokenType.AND, TokenType.OR, TokenType.SEMI, TokenType.COMMA, TokenType.RPAREN
    );

    // Conjuntos de sincronizacao pequenos (antes criados dentro dos metodos)
    private static final Set<TokenType> SYNC_RPAREN = EnumSet.of(TokenType.RPAREN);
    private static final Set<TokenType> SYNC_SEMI   = EnumSet.of(TokenType.SEMI);
    private static final Set<TokenType> SYNC_TIPO   = EnumSet.of(
            TokenType.ID, TokenType.RPAREN, TokenType.COMMA
    );

    // Sincronizacoes adicionais combinando FIRST e FOLLOW
    private static final Set<TokenType> SYNC_LISTA_COMANDOS;
    static {
        SYNC_LISTA_COMANDOS = EnumSet.copyOf(FIRST_COMANDO);
        SYNC_LISTA_COMANDOS.addAll(FOLLOW_LISTA_COMANDOS);
    }

    private static final Set<TokenType> SYNC_LISTA_DECLARACOES;
    static {
        SYNC_LISTA_DECLARACOES = EnumSet.copyOf(FIRST_DECLARACAO);
        SYNC_LISTA_DECLARACOES.addAll(FOLLOW_LISTA_DECLARACOES);
    }

    // ------------------------------------------------------------------
    //  EXPRESSÕES
    // ------------------------------------------------------------------

    // Fator ::= "(" Expressao ")" | ID FatorIdResto | INT_LIT | DOUBLE_LIT | STRING_LIT | CHAR_LIT | "true" | "false"
    private void parseFator() {
        TokenType t = lookahead.type();
        if (t == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            parseExpressao();
            match(TokenType.RPAREN);
        } else if (t == TokenType.ID) {
            match(TokenType.ID);
            parseFatorIdResto();
        } else if (t == TokenType.INT_LIT) { match(TokenType.INT_LIT); }
          else if (t == TokenType.DOUBLE_LIT) { match(TokenType.DOUBLE_LIT); }
          else if (t == TokenType.STRING_LIT) { match(TokenType.STRING_LIT); }
          else if (t == TokenType.CHAR_LIT) { match(TokenType.CHAR_LIT); }
          else if (t == TokenType.TRUE) { match(TokenType.TRUE); }
          else if (t == TokenType.FALSE) { match(TokenType.FALSE); }
          else {
            error("Fator invalido: achei " + t);
            sincronizar(FOLLOW_FATOR);
        }
    }

    private void parseFatorIdResto() {
        if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            parseOpcionalArgs();
            match(TokenType.RPAREN);
        } else if (FOLLOW_FATOR.contains(lookahead.type())) {
            // ε
        } else {
            error("Esperava '(' ou continuacao de expressao, achei " + lookahead.type());
            sincronizar(FOLLOW_FATOR);
        }
    }

    private void parseExpUnaria() {
        TokenType t = lookahead.type();
        if (t == TokenType.MINUS || t == TokenType.NOT) {
            match(t);
            parseExpUnaria();
        } else if (FIRST_FATOR.contains(t)) {
            parseFator();
        } else {
            error("Esperava operador unario ou fator, achei " + t);
            sincronizar(FOLLOW_FATOR);
        }
    }

    private void parseExpPot() {
        parseExpUnaria();
        if (lookahead.type() == TokenType.POW) {
            match(TokenType.POW);
            parseExpPot();
        }
        if (!FOLLOW_EXP_POT.contains(lookahead.type())) {
            error("Token inesperado apos expressao de potencia: " + lookahead.type());
            sincronizar(FOLLOW_EXP_POT);
        }
    }

    private void parseExpMult() {
        parseExpPot();
        while (lookahead.type() == TokenType.STAR || lookahead.type() == TokenType.SLASH || lookahead.type() == TokenType.PERCENT) {
            match(lookahead.type());
            parseExpPot();
        }
        if (!FOLLOW_EXP_MULT.contains(lookahead.type())) {
            error("Token inesperado apos multiplicacao: " + lookahead.type());
            sincronizar(FOLLOW_EXP_MULT);
        }
    }

    private void parseExpAdd() {
        parseExpMult();
        while (lookahead.type() == TokenType.PLUS || lookahead.type() == TokenType.MINUS) {
            match(lookahead.type());
            parseExpMult();
        }
        if (!FOLLOW_EXP_ADD.contains(lookahead.type())) {
            error("Token inesperado apos adicao: " + lookahead.type());
            sincronizar(FOLLOW_EXP_ADD);
        }
    }

    private void parseExpRelacional() {
        parseExpAdd();
        while (lookahead.type() == TokenType.LT || lookahead.type() == TokenType.GT ||
               lookahead.type() == TokenType.LE || lookahead.type() == TokenType.GE) {
            match(lookahead.type());
            parseExpAdd();
        }
        if (!FOLLOW_EXP_RELACIONAL.contains(lookahead.type())) {
            error("Token inesperado apos relacional: " + lookahead.type());
            sincronizar(FOLLOW_EXP_RELACIONAL);
        }
    }

    private void parseExpIgualdade() {
        parseExpRelacional();
        while (lookahead.type() == TokenType.EQ_EQ || lookahead.type() == TokenType.NEQ) {
            match(lookahead.type());
            parseExpRelacional();
        }
        if (!FOLLOW_EXP_IGUALDADE.contains(lookahead.type())) {
            error("Token inesperado apos igualdade: " + lookahead.type());
            sincronizar(FOLLOW_EXP_IGUALDADE);
        }
    }

    private void parseExpAND() {
        parseExpIgualdade();
        while (lookahead.type() == TokenType.AND) {
            match(TokenType.AND);
            parseExpIgualdade();
        }
        if (!FOLLOW_EXP_AND.contains(lookahead.type())) {
            error("Token inesperado apos AND: " + lookahead.type());
            sincronizar(FOLLOW_EXP_AND);
        }
    }

    private void parseExpOR() {
        parseExpAND();
        while (lookahead.type() == TokenType.OR) {
            match(TokenType.OR);
            parseExpAND();
        }
        if (!FOLLOW_EXPRESSAO.contains(lookahead.type())) {
            error("Token inesperado no fim da expressao: " + lookahead.type());
            sincronizar(FOLLOW_EXPRESSAO);
        }
    }

    private void parseExpressao() {
        parseExpOR();
    }

    private void parseOpcionalArgs() {
        if (FIRST_EXPRESSAO.contains(lookahead.type())) {
            parseListaArgs();
        } else if (lookahead.type() == TokenType.RPAREN) {
            // ε legitimo, o match() eh feito por quem chamou
        } else {
            error("Esperava expressao ou ')', achei " + lookahead.type());
            sincronizar(SYNC_RPAREN);
        }
    }

    private void parseListaArgs() {
        parseExpressao();
        while (lookahead.type() == TokenType.COMMA) {
            match(TokenType.COMMA);
            parseExpressao();
        }
    }

    // ==================================================================
    // COMANDOS
    // ==================================================================

    private void parseBloco() {
        match(TokenType.LBRACE);
        parseListaComandos();
        match(TokenType.RBRACE);
    }

    private void parseListaComandos() {
        while (true) {
            if (FIRST_COMANDO.contains(lookahead.type())) {
                parseComando();
            } else if (FOLLOW_LISTA_COMANDOS.contains(lookahead.type())
                    || lookahead.type() == TokenType.EOF) {
                break; // ε; no EOF (bloco nao fechado) o match(RBRACE) do parseBloco reporta o erro
            } else {
                error("Comando invalido dentro do bloco: " + lookahead.type());
                sincronizar(SYNC_LISTA_COMANDOS);
            }
        }
    }

    private void parseComando() {
        TokenType t = lookahead.type();
        if (t == TokenType.CONST) {
            parseDeclConst();
        } else if (FIRST_TIPO.contains(t)) {
            parseDeclVarLocal();
        } else if (t == TokenType.ID) {
            parseCmdId();
        } else if (t == TokenType.IF) {
            parseCmdIf();
        } else if (t == TokenType.WHILE) {
            parseCmdWhile();
        } else if (t == TokenType.RETURN) {
            parseCmdReturn();
        } else if (t == TokenType.PRINT) {
            parseCmdPrint();
        } else if (t == TokenType.READ) {
            parseCmdRead();
        } else {
            // Esse else eh apenas seguranca, pois parseComando so eh chamado apos checar FIRST
            error("Comando invalido: " + t);
            sincronizar(FOLLOW_COMANDO);
        }
    }

    private void parseDeclVarLocal() {
        parseTipo();
        match(TokenType.ID);
        parseAtribuicaoOpcional();
        match(TokenType.SEMI);
    }

    private void parseAtribuicaoOpcional() {
        if (lookahead.type() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            parseExpressao();
        } else if (lookahead.type() == TokenType.SEMI) {
            // ε
        } else {
            error("Esperava '=' ou ';', achei " + lookahead.type());
            sincronizar(SYNC_SEMI);
        }
    }

    private void parseCmdId() {
        match(TokenType.ID);
        parseCmdIdResto();
    }

    private void parseCmdIdResto() {
        if (lookahead.type() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            parseExpressao();
            match(TokenType.SEMI);
        } else if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            parseOpcionalArgs();
            match(TokenType.RPAREN);
            match(TokenType.SEMI);
        } else {
            error("Apos o ID de comando, esperava '=' ou '(', achei " + lookahead.type());
            sincronizar(FOLLOW_COMANDO);
        }
    }

    private void parseCmdIf() {
        match(TokenType.IF);
        match(TokenType.LPAREN);
        parseExpressao();
        match(TokenType.RPAREN);
        parseBloco();
        parseListaElif();
        parseElseOpcional();
    }

    private void parseListaElif() {
        while (lookahead.type() == TokenType.ELIF) {
            match(TokenType.ELIF);
            match(TokenType.LPAREN);
            parseExpressao();
            match(TokenType.RPAREN);
            parseBloco();
        }
    }

    private void parseElseOpcional() {
        if (lookahead.type() == TokenType.ELSE) {
            match(TokenType.ELSE);
            parseBloco();
        }
    }

    private void parseCmdWhile() {
        match(TokenType.WHILE);
        match(TokenType.LPAREN);
        parseExpressao();
        match(TokenType.RPAREN);
        parseBloco();
    }

    private void parseCmdReturn() {
        match(TokenType.RETURN);
        parseRetornoOpcional();
        match(TokenType.SEMI);
    }

    private void parseRetornoOpcional() {
        if (FIRST_EXPRESSAO.contains(lookahead.type())) {
            parseExpressao();
        } else if (lookahead.type() == TokenType.SEMI) {
            // ε
        } else {
            error("Esperava expressao ou ';', achei " + lookahead.type());
            sincronizar(SYNC_SEMI);
        }
    }

    private void parseCmdPrint() {
        match(TokenType.PRINT);
        match(TokenType.LPAREN);
        parseExpressao();
        match(TokenType.RPAREN);
        match(TokenType.SEMI);
    }

    private void parseCmdRead() {
        match(TokenType.READ);
        match(TokenType.LPAREN);
        match(TokenType.ID);
        match(TokenType.RPAREN);
        match(TokenType.SEMI);
    }

    // ==================================================================
    // DECLARAÇÕES (Globais)
    // ==================================================================

    private void parseTipo() {
        TokenType t = lookahead.type();
        if (FIRST_TIPO.contains(t)) {
            match(t);
        } else {
            error("Esperava um tipo, achei " + t);
            sincronizar(SYNC_TIPO);
        }
    }

    private void parseDeclConst() {
        match(TokenType.CONST);
        parseTipo();
        match(TokenType.ID);
        match(TokenType.ASSIGN);
        parseExpressao();
        match(TokenType.SEMI);
    }

    private void parseDeclaracao() {
        TokenType t = lookahead.type();
        if (t == TokenType.CONST) {
            parseDeclConst();
        } else if (FIRST_TIPO.contains(t)) {
            parseDeclVarFuncao();
        } else if (t == TokenType.VOID) {
            parseDeclVoidFuncao();
        } else {
            error("Declaracao global invalida, achei " + t);
            sincronizar(FOLLOW_DECLARACAO);
        }
    }

    private void parseDeclVarFuncao() {
        parseTipo();
        match(TokenType.ID);
        parseDeclResto();
    }

    private void parseDeclResto() {
        if (lookahead.type() == TokenType.ASSIGN || lookahead.type() == TokenType.SEMI) {
            parseAtribuicaoOpcional();
            match(TokenType.SEMI);
        } else if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            parseOpcionalParams();
            match(TokenType.RPAREN);
            parseBloco();
        } else {
            error("Esperava '=', ';' ou '(', achei " + lookahead.type());
            sincronizar(FOLLOW_DECLARACAO);
        }
    }

    private void parseDeclVoidFuncao() {
        match(TokenType.VOID);
        match(TokenType.ID);
        match(TokenType.LPAREN);
        parseOpcionalParams();
        match(TokenType.RPAREN);
        parseBloco();
    }

    private void parseOpcionalParams() {
        if (FIRST_TIPO.contains(lookahead.type())) {
            parseListaParams();
        } else if (lookahead.type() == TokenType.RPAREN) {
            // ε
        } else {
            error("Esperava parametros ou ')', achei " + lookahead.type());
            sincronizar(SYNC_RPAREN);
        }
    }

    private void parseListaParams() {
        parseTipo();
        match(TokenType.ID);
        while (lookahead.type() == TokenType.COMMA) {
            match(TokenType.COMMA);
            parseTipo();
            match(TokenType.ID);
        }
    }

    private void parsePrograma() {
        while (true) {
            if (FIRST_DECLARACAO.contains(lookahead.type())) {
                parseDeclaracao();
            } else if (FOLLOW_LISTA_DECLARACOES.contains(lookahead.type())) {
                break; // ε (EOF)
            } else {
                error("Token invalido no nivel global: " + lookahead.type());
                sincronizar(SYNC_LISTA_DECLARACOES);
            }
        }
    }
}