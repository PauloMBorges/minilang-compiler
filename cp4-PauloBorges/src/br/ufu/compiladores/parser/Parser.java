package br.ufu.compiladores.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import br.ufu.compiladores.lexer.Scanner;
import br.ufu.compiladores.lexer.Token;
import br.ufu.compiladores.lexer.TokenType;
import br.ufu.compiladores.ast.*; // Certifique-se de que os nós AST estão visíveis

/*
 * Parser recursivo descendente - Checkpoint 3 (Construção da AST).
 */
public class Parser {

    public record ParseResult(Program ast, List<SyntaxError> errors) {}

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

    public ParseResult parse() {
        Program program = parsePrograma();
        match(TokenType.EOF);
        
        // Só devolve a árvore se não houver erros sintáticos NEM erros léxicos
        if (errors.isEmpty() && scanner.getErrors().isEmpty()) {
            return new ParseResult(program, errors);
        } else {
            return new ParseResult(null, errors); // Árvore nula se houver erro
        }
    }

    /** Consome o terminal esperado e retorna o Token consumido. Reporta erro se falhar. */
    private Token match(TokenType esperado) {
        Token atual = lookahead;
        if (lookahead.type() == esperado) {
            lookahead = scanner.nextToken();
            return atual;
        } else {
            error("esperava " + esperado + ", achei " + lookahead.type());
            return null; // Retorna null para sinalizar falha no match
        }
    }

    /** Registra um erro na posicao do token atual  */
    private void error(String msg) {
        errors.add(new SyntaxError(msg, lookahead.line(), lookahead.col()));
    }
    
    /** Registra um erro informando linha e coluna especificas */
    private void error(String msg, int line, int col) {
        errors.add(new SyntaxError(msg, line, col));
    }

    /** Modo panico: descarta tokens ate um do conjunto de sincronizacao ou EOF  */
    private void sincronizar(Set<TokenType> sync) {
        while (!sync.contains(lookahead.type())
                && lookahead.type() != TokenType.EOF) {
            lookahead = scanner.nextToken();  // descarta o token
        }
    }
    
    // Conversores seguros para literais
    private long parseLongSafe(Token t) {
        try {
            return Long.parseLong(t.lexeme().replace("_", ""));
        } catch (NumberFormatException e) {
            error("Numero muito grande para long: " + t.lexeme(), t.line(), t.col());
            return 0L;
        }
    }

    private double parseDoubleSafe(Token t) {
        return Double.parseDouble(t.lexeme().replace("_", ""));
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

    private static final Set<TokenType> SYNC_RPAREN = EnumSet.of(TokenType.RPAREN);
    private static final Set<TokenType> SYNC_SEMI   = EnumSet.of(TokenType.SEMI);
    private static final Set<TokenType> SYNC_TIPO   = EnumSet.of(
            TokenType.ID, TokenType.RPAREN, TokenType.COMMA
    );

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

    private Expr parseFator() {
        TokenType t = lookahead.type();
        if (t == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            Expr expr = parseExpressao();
            match(TokenType.RPAREN);
            return expr;
        } else if (t == TokenType.ID) {
            Token id = match(TokenType.ID);
            return parseFatorIdResto(id);
        } else if (t == TokenType.INT_LIT) {
            Token tok = match(TokenType.INT_LIT);
            if (tok == null) return null;
            return new IntLiteral(tok.line(), tok.col(), parseLongSafe(tok), tok.lexeme());
        } else if (t == TokenType.DOUBLE_LIT) {
            Token tok = match(TokenType.DOUBLE_LIT);
            if (tok == null) return null;
            return new DoubleLiteral(tok.line(), tok.col(), parseDoubleSafe(tok), tok.lexeme());
        } else if (t == TokenType.STRING_LIT) {
            Token tok = match(TokenType.STRING_LIT);
            if (tok == null) return null;
            String valorString = tok.lexeme(); 
            return new StringLiteral(tok.line(), tok.col(), valorString, tok.lexeme());
        } else if (t == TokenType.CHAR_LIT) {
            Token tok = match(TokenType.CHAR_LIT);
            if (tok == null) return null;
            char c = tok.lexeme().charAt(0); // Correção: scanner já entrega limpo
            return new CharLiteral(tok.line(), tok.col(), c, tok.lexeme());
        } else if (t == TokenType.TRUE) {
            Token tok = match(TokenType.TRUE);
            if (tok == null) return null;
            return new BoolLiteral(tok.line(), tok.col(), true, tok.lexeme());
        } else if (t == TokenType.FALSE) {
            Token tok = match(TokenType.FALSE);
            if (tok == null) return null;
            return new BoolLiteral(tok.line(), tok.col(), false, tok.lexeme());
        } else {
            error("Fator invalido: achei " + t);
            sincronizar(FOLLOW_FATOR);
            return null;
        }
    }

    private Expr parseFatorIdResto(Token id) {
        if (id == null) return null;
        
        if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            List<Expr> args = parseOpcionalArgs();
            match(TokenType.RPAREN);
            return new CallExpr(id.line(), id.col(), id.lexeme(), args != null ? args : new ArrayList<>());
        } else if (FOLLOW_FATOR.contains(lookahead.type())) {
            // ε
            return new IdExpr(id.line(), id.col(), id.lexeme());
        } else {
            error("Esperava '(' ou continuacao de expressao, achei " + lookahead.type());
            sincronizar(FOLLOW_FATOR);
            return null;
        }
    }

    private Expr parseExpUnaria() {
        TokenType t = lookahead.type();
        if (t == TokenType.MINUS || t == TokenType.NOT) {
            Token op = match(t);
            Expr expr = parseExpUnaria();
            if (op == null || expr == null) return null;
            return new UnExpr(op.line(), op.col(), op.lexeme(), expr);
        } else if (FIRST_FATOR.contains(t)) {
            return parseFator();
        } else {
            error("Esperava operador unario ou fator, achei " + t);
            sincronizar(FOLLOW_FATOR);
            return null;
        }
    }

    private Expr parseExpPot() {
        Expr esq = parseExpUnaria();
        if (lookahead.type() == TokenType.POW) {
            Token op = match(TokenType.POW);
            Expr dir = parseExpPot();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_POT.contains(lookahead.type())) {
            error("Token inesperado apos expressao de potencia: " + lookahead.type());
            sincronizar(FOLLOW_EXP_POT);
            return null;
        }
        return esq;
    }

    private Expr parseExpMult() {
        Expr esq = parseExpPot();
        while (lookahead.type() == TokenType.STAR || lookahead.type() == TokenType.SLASH || lookahead.type() == TokenType.PERCENT) {
            Token op = match(lookahead.type());
            Expr dir = parseExpPot();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_MULT.contains(lookahead.type())) {
            error("Token inesperado apos multiplicacao: " + lookahead.type());
            sincronizar(FOLLOW_EXP_MULT);
            return null;
        }
        return esq;
    }

    private Expr parseExpAdd() {
        Expr esq = parseExpMult();
        while (lookahead.type() == TokenType.PLUS || lookahead.type() == TokenType.MINUS) {
            Token op = match(lookahead.type());
            Expr dir = parseExpMult();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_ADD.contains(lookahead.type())) {
            error("Token inesperado apos adicao: " + lookahead.type());
            sincronizar(FOLLOW_EXP_ADD);
            return null;
        }
        return esq;
    }

    private Expr parseExpRelacional() {
        Expr esq = parseExpAdd();
        while (lookahead.type() == TokenType.LT || lookahead.type() == TokenType.GT ||
               lookahead.type() == TokenType.LE || lookahead.type() == TokenType.GE) {
            Token op = match(lookahead.type());
            Expr dir = parseExpAdd();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_RELACIONAL.contains(lookahead.type())) {
            error("Token inesperado apos relacional: " + lookahead.type());
            sincronizar(FOLLOW_EXP_RELACIONAL);
            return null;
        }
        return esq;
    }

    private Expr parseExpIgualdade() {
        Expr esq = parseExpRelacional();
        while (lookahead.type() == TokenType.EQ_EQ || lookahead.type() == TokenType.NEQ) {
            Token op = match(lookahead.type());
            Expr dir = parseExpRelacional();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_IGUALDADE.contains(lookahead.type())) {
            error("Token inesperado apos igualdade: " + lookahead.type());
            sincronizar(FOLLOW_EXP_IGUALDADE);
            return null;
        }
        return esq;
    }

    private Expr parseExpAND() {
        Expr esq = parseExpIgualdade();
        while (lookahead.type() == TokenType.AND) {
            Token op = match(TokenType.AND);
            Expr dir = parseExpIgualdade();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXP_AND.contains(lookahead.type())) {
            error("Token inesperado apos AND: " + lookahead.type());
            sincronizar(FOLLOW_EXP_AND);
            return null;
        }
        return esq;
    }

    private Expr parseExpOR() {
        Expr esq = parseExpAND();
        while (lookahead.type() == TokenType.OR) {
            Token op = match(TokenType.OR);
            Expr dir = parseExpAND();
            if (esq != null && op != null && dir != null) {
                esq = new BinExpr(esq.line(), esq.col(), esq, op.lexeme(), dir);
            } else {
                esq = null;
            }
        }
        if (!FOLLOW_EXPRESSAO.contains(lookahead.type())) {
            error("Token inesperado no fim da expressao: " + lookahead.type());
            sincronizar(FOLLOW_EXPRESSAO);
            return null; // Força invalidar se a expressao estiver corrompida no fim
        }
        return esq;
    }

    private Expr parseExpressao() {
        return parseExpOR();
    }

    private List<Expr> parseOpcionalArgs() {
        if (FIRST_EXPRESSAO.contains(lookahead.type())) {
            return parseListaArgs();
        } else if (lookahead.type() == TokenType.RPAREN) {
            return new ArrayList<>(); // ε
        } else {
            error("Esperava expressao ou ')', achei " + lookahead.type());
            sincronizar(SYNC_RPAREN);
            return new ArrayList<>();
        }
    }

    private List<Expr> parseListaArgs() {
        List<Expr> args = new ArrayList<>();
        Expr first = parseExpressao();
        if (first != null) args.add(first);
        
        while (lookahead.type() == TokenType.COMMA) {
            match(TokenType.COMMA);
            Expr arg = parseExpressao();
            if (arg != null) args.add(arg);
        }
        return args;
    }

    // ==================================================================
    // COMANDOS
    // ==================================================================

    private BlockCmd parseBloco() {
        Token t = match(TokenType.LBRACE);
        List<Cmd> cmds = parseListaComandos();
        match(TokenType.RBRACE);
        if (t == null) return null;
        return new BlockCmd(t.line(), t.col(), cmds != null ? cmds : new ArrayList<>());
    }

    private List<Cmd> parseListaComandos() {
        List<Cmd> cmds = new ArrayList<>();
        while (true) {
            if (FIRST_COMANDO.contains(lookahead.type())) {
                Cmd cmd = parseComando();
                if (cmd != null) cmds.add(cmd);
            } else if (FOLLOW_LISTA_COMANDOS.contains(lookahead.type())
                    || lookahead.type() == TokenType.EOF) {
                break;
            } else {
                error("Comando invalido dentro do bloco: " + lookahead.type());
                sincronizar(SYNC_LISTA_COMANDOS);
            }
        }
        return cmds;
    }

    private Cmd parseComando() {
        TokenType t = lookahead.type();
        if (t == TokenType.CONST) {
            VarDecl decl = parseDeclConst();
            return decl != null ? new DeclCmd(decl.line(), decl.col(), decl) : null;
        } else if (FIRST_TIPO.contains(t)) {
            VarDecl decl = parseDeclVarLocal();
            return decl != null ? new DeclCmd(decl.line(), decl.col(), decl) : null;
        } else if (t == TokenType.ID) {
            return parseCmdId();
        } else if (t == TokenType.IF) {
            return parseCmdIf();
        } else if (t == TokenType.WHILE) {
            return parseCmdWhile();
        } else if (t == TokenType.RETURN) {
            return parseCmdReturn();
        } else if (t == TokenType.PRINT) {
            return parseCmdPrint();
        } else if (t == TokenType.READ) {
            return parseCmdRead();
        } else {
            error("Comando invalido: " + t);
            sincronizar(FOLLOW_COMANDO);
            return null;
        }
    }

    private VarDecl parseDeclVarLocal() {
        PrimitiveType tipo = parseTipo();
        Token id = match(TokenType.ID);
        Expr init = parseAtribuicaoOpcional();
        match(TokenType.SEMI);
        if (tipo == null || id == null) return null;
        return new VarDecl(tipo.line(), tipo.col(), false, tipo, id.lexeme(), init);
    }

    private Expr parseAtribuicaoOpcional() {
        if (lookahead.type() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            return parseExpressao();
        } else if (lookahead.type() == TokenType.SEMI) {
            return null; // ε
        } else {
            error("Esperava '=' ou ';', achei " + lookahead.type());
            sincronizar(SYNC_SEMI);
            return null;
        }
    }

    private Cmd parseCmdId() {
        Token id = match(TokenType.ID);
        if (id == null) return null;
        return parseCmdIdResto(id);
    }

    private Cmd parseCmdIdResto(Token id) {
        if (lookahead.type() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            Expr expr = parseExpressao();
            match(TokenType.SEMI);
            if (expr == null) return null;
            return new AssignCmd(id.line(), id.col(), id.lexeme(), expr);
        } else if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            List<Expr> args = parseOpcionalArgs();
            match(TokenType.RPAREN);
            match(TokenType.SEMI);
            return new CallCmd(id.line(), id.col(), id.lexeme(), args != null ? args : new ArrayList<>());
        } else {
            error("Apos o ID de comando, esperava '=' ou '(', achei " + lookahead.type());
            sincronizar(FOLLOW_COMANDO);
            return null;
        }
    }

    private IfCmd parseCmdIf() {
        Token ifTok = match(TokenType.IF);
        match(TokenType.LPAREN);
        Expr cond = parseExpressao();
        match(TokenType.RPAREN);
        BlockCmd thenCmd = parseBloco();
        List<ElifBranch> elifs = parseListaElif();
        BlockCmd elseCmd = parseElseOpcional();
        
        if (ifTok == null || cond == null || thenCmd == null) return null;
        return new IfCmd(ifTok.line(), ifTok.col(), cond, thenCmd, elifs != null ? elifs : new ArrayList<>(), elseCmd);
    }

    private List<ElifBranch> parseListaElif() {
        List<ElifBranch> elifs = new ArrayList<>();
        while (lookahead.type() == TokenType.ELIF) {
            Token elifTok = match(TokenType.ELIF);
            match(TokenType.LPAREN);
            Expr cond = parseExpressao();
            match(TokenType.RPAREN);
            BlockCmd block = parseBloco();
            if (elifTok != null && cond != null && block != null) {
                elifs.add(new ElifBranch(elifTok.line(), elifTok.col(), cond, block));
            }
        }
        return elifs;
    }

    private BlockCmd parseElseOpcional() {
        if (lookahead.type() == TokenType.ELSE) {
            match(TokenType.ELSE);
            return parseBloco();
        }
        return null;
    }

    private WhileCmd parseCmdWhile() {
        Token whileTok = match(TokenType.WHILE);
        match(TokenType.LPAREN);
        Expr cond = parseExpressao();
        match(TokenType.RPAREN);
        BlockCmd body = parseBloco();
        
        if (whileTok == null || cond == null || body == null) return null;
        return new WhileCmd(whileTok.line(), whileTok.col(), cond, body);
    }

    private ReturnCmd parseCmdReturn() {
        Token retTok = match(TokenType.RETURN);
        Expr expr = parseRetornoOpcional();
        match(TokenType.SEMI);
        
        if (retTok == null) return null;
        return new ReturnCmd(retTok.line(), retTok.col(), expr);
    }

    private Expr parseRetornoOpcional() {
        if (FIRST_EXPRESSAO.contains(lookahead.type())) {
            return parseExpressao();
        } else if (lookahead.type() == TokenType.SEMI) {
            return null; // ε
        } else {
            error("Esperava expressao ou ';', achei " + lookahead.type());
            sincronizar(SYNC_SEMI);
            return null;
        }
    }

    private PrintCmd parseCmdPrint() {
        Token printTok = match(TokenType.PRINT);
        match(TokenType.LPAREN);
        Expr expr = parseExpressao();
        match(TokenType.RPAREN);
        match(TokenType.SEMI);
        
        if (printTok == null || expr == null) return null;
        return new PrintCmd(printTok.line(), printTok.col(), expr);
    }

    private ReadCmd parseCmdRead() {
        Token readTok = match(TokenType.READ);
        match(TokenType.LPAREN);
        Token id = match(TokenType.ID);
        match(TokenType.RPAREN);
        match(TokenType.SEMI);
        
        if (readTok == null || id == null) return null;
        return new ReadCmd(readTok.line(), readTok.col(), id.lexeme());
    }

    // ==================================================================
    // DECLARAÇÕES (Globais)
    // ==================================================================

    private PrimitiveType parseTipo() {
        TokenType t = lookahead.type();
        if (FIRST_TIPO.contains(t)) {
            Token tok = match(t);
            if (tok == null) return null;
            return new PrimitiveType(tok.line(), tok.col(), tok.lexeme());
        } else {
            error("Esperava um tipo, achei " + t);
            sincronizar(SYNC_TIPO);
            return null;
        }
    }

    private VarDecl parseDeclConst() {
        Token constTok = match(TokenType.CONST);
        PrimitiveType tipo = parseTipo();
        Token id = match(TokenType.ID);
        match(TokenType.ASSIGN);
        Expr init = parseExpressao();
        match(TokenType.SEMI);
        
        if (constTok == null || tipo == null || id == null || init == null) return null;
        return new VarDecl(constTok.line(), constTok.col(), true, tipo, id.lexeme(), init);
    }

    private Decl parseDeclaracao() {
        TokenType t = lookahead.type();
        if (t == TokenType.CONST) {
            return parseDeclConst();
        } else if (FIRST_TIPO.contains(t)) {
            return parseDeclVarFuncao();
        } else if (t == TokenType.VOID) {
            return parseDeclVoidFuncao();
        } else {
            error("Declaracao global invalida, achei " + t);
            sincronizar(FOLLOW_DECLARACAO);
            return null;
        }
    }

    private Decl parseDeclVarFuncao() {
        PrimitiveType tipo = parseTipo();
        Token id = match(TokenType.ID);
        if (tipo == null || id == null) {
            return null; // Apenas aborta a criacao do no para não dar NullPointerException
        }
        return parseDeclResto(tipo, id);
    }

    private Decl parseDeclResto(PrimitiveType tipo, Token id) {
        if (lookahead.type() == TokenType.ASSIGN || lookahead.type() == TokenType.SEMI) {
            Expr init = parseAtribuicaoOpcional();
            match(TokenType.SEMI);
            return new VarDecl(tipo.line(), tipo.col(), false, tipo, id.lexeme(), init);
        } else if (lookahead.type() == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            List<Param> params = parseOpcionalParams();
            match(TokenType.RPAREN);
            BlockCmd body = parseBloco();
            if (body == null) return null;
            return new FuncDecl(tipo.line(), tipo.col(), tipo, id.lexeme(), params != null ? params : new ArrayList<>(), body);
        } else {
            error("Esperava '=', ';' ou '(', achei " + lookahead.type());
            sincronizar(FOLLOW_DECLARACAO);
            return null;
        }
    }

    private ProcDecl parseDeclVoidFuncao() {
        Token voidTok = match(TokenType.VOID);
        Token id = match(TokenType.ID);
        match(TokenType.LPAREN);
        List<Param> params = parseOpcionalParams();
        match(TokenType.RPAREN);
        BlockCmd body = parseBloco();
        
        if (voidTok == null || id == null || body == null) return null;
        return new ProcDecl(voidTok.line(), voidTok.col(), id.lexeme(), params != null ? params : new ArrayList<>(), body);
    }

    private List<Param> parseOpcionalParams() {
        if (FIRST_TIPO.contains(lookahead.type())) {
            return parseListaParams();
        } else if (lookahead.type() == TokenType.RPAREN) {
            return new ArrayList<>(); // ε
        } else {
            error("Esperava parametros ou ')', achei " + lookahead.type());
            sincronizar(SYNC_RPAREN);
            return new ArrayList<>();
        }
    }

    private List<Param> parseListaParams() {
        List<Param> params = new ArrayList<>();
        PrimitiveType tipo = parseTipo();
        Token id = match(TokenType.ID);
        if (tipo != null && id != null) {
            params.add(new Param(tipo.line(), tipo.col(), tipo, id.lexeme()));
        }
        
        while (lookahead.type() == TokenType.COMMA) {
            match(TokenType.COMMA);
            PrimitiveType t = parseTipo();
            Token i = match(TokenType.ID);
            if (t != null && i != null) {
                params.add(new Param(t.line(), t.col(), t, i.lexeme()));
            }
        }
        return params;
    }

    private Program parsePrograma() {
        int line = lookahead.line();
        int col = lookahead.col();
        List<Decl> decls = new ArrayList<>();
        
        while (true) {
            if (FIRST_DECLARACAO.contains(lookahead.type())) {
                Decl decl = parseDeclaracao();
                if (decl != null) decls.add(decl);
            } else if (FOLLOW_LISTA_DECLARACOES.contains(lookahead.type())) {
                break; // ε (EOF)
            } else {
                error("Token invalido no nivel global: " + lookahead.type());
                sincronizar(SYNC_LISTA_DECLARACOES);
            }
        }
        return new Program(line, col, decls);
    }
}