package br.ufu.compiladores.lexer;

/* Analisador lexico (scanner) da MiniLang (manual) */

public class Scanner {

    private final String source;
    private int pos  = 0;
    private int line = 1;
    private int col  = 1;

    public Scanner(String source) {
        this.source = source;
    }

    // ------------------------------------------------------------------
    //  MECANICA DE BUFFER 
    // ------------------------------------------------------------------

    /** Ha mais caracteres para ler? */
    private boolean hasNext() {
        return pos < source.length();
    }

    /** Olha o caractere atual SEM consumir. Retorna '\0' no fim. */
    private char peek() {
        return hasNext() ? source.charAt(pos) : '\0';
    }

    /** Olha o proximo caractere (lookahead de 1) SEM consumir. */
    private char peekNext() {
        return (pos + 1 < source.length()) ? source.charAt(pos + 1) : '\0';
    }

    /** Consome o caractere atual, move o cursor e atualiza linha/coluna. */
    private char advance() {
        char c = source.charAt(pos++);
        if (c == '\n') { line++; col = 1; } else { col++; }
        return c;
    }

    // ------------------------------------------------------------------
    //  PONTO DE ENTRADA
    // ------------------------------------------------------------------

    /**
     * Produz o proximo token do fluxo. É o "START" do autômato:
     * pula o que deve ser ignorado, olha o primeiro caractere e despacha
     * para o AFD correto (maximal munch acontece dentro de cada scanXxx).
     */
    public Token nextToken() {
        skipWhitespaceAndComments();

        if (!hasNext()) {
            return new Token(TokenType.EOF, "", line, col);
        }

        char c = peek();

        // Despacho: o primeiro caractere ja decide qual AFD entra.
        if (isLetter(c) || c == '_')  return scanIdentifierOrKeyword();
        if (isDigit(c))               return scanNumber();
        if (c == '"')                 return scanString();
        // operadores e pontuacao: tudo o mais cai aqui
        return scanOperatorOrPunctuation();
    }

    // ------------------------------------------------------------------
    //  IGNORADOS: espacos em branco e comentario de linha '#'
    // ------------------------------------------------------------------

    private void skipWhitespaceAndComments() {
        while (hasNext()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
            } else if (c == '#') {
                // Comentario de linha: avanca ate o fim da linha (Aula 02).
                while (hasNext() && peek() != '\n') advance();
            } else {
                break;
            }
        }
    }

    // ------------------------------------------------------------------
    //  AFDs — IMPLEMENTACAO SUA (esqueletos com roteiro)
    // ------------------------------------------------------------------

    /**
     * TOKEN 1/2 — Identificador ou palavra reservada.
     * Regra: (letra | '_') (letra | digito | '_')*
     *
     * ROTEIRO DO AFD:
     *   - marque a posicao inicial (linha/coluna do token) e o pos inicial;
     *   - consuma enquanto peek() for letra, digito ou '_'  (maximal munch);
     *   - extraia o lexema = source.substring(inicio, pos);
     *   - TokenType t = ReservedWords.classify(lexeme);
     *   - retorne new Token(t, lexeme, ...).
     */
    private Token scanIdentifierOrKeyword() {
        // TODO (seu): implementar o AFD de identificador + consulta a ReservedWords.
        throw new UnsupportedOperationException("scanIdentifierOrKeyword: implementar (Checkpoint 1)");
    }

    /**
     * TOKEN 5 — Literal numerico: INT_LIT ou DOUBLE_LIT.
     * INT_LIT:    digito (digito | '_')*     ('_' apenas ENTRE digitos)
     * DOUBLE_LIT: digito+ ('.' digito+)? (('e'|'E') ('+'|'-')? digito+)?
     *
     * ROTEIRO DO AFD:
     *   - consuma a parte inteira (digitos, permitindo '_' entre digitos);
     *     cuidado com '1_', '_1', '1__0' -> erro;
     *   - se peek()=='.', tente a parte fracionaria: exige digito depois do ponto
     *     ('3.' sozinho e' erro);
     *   - se peek() e' 'e'/'E', tente o expoente: sinal opcional + digito+;
     *   - o estado de aceitacao (com ou sem '.'/'e') define INT_LIT vs DOUBLE_LIT.
     */
    private Token scanNumber() {
        // TODO (seu): implementar o AFD de literais numericos.
        throw new UnsupportedOperationException("scanNumber: implementar (Checkpoint 1)");
    }

    /**
     * TOKEN 3 — String literal.
     * Regra: " (qualquer caractere != ")* "
     *
     * ROTEIRO DO AFD:
     *   - consuma a aspa de abertura;
     *   - self-loop: consuma tudo que nao for '"';
     *   - CASO DE ERRO: se chegar a '\n' ou ao EOF sem fechar -> LexicalError
     *     (reporte a posicao; decida sua estrategia de recuperacao);
     *   - consuma a aspa de fechamento;
     *   - o lexeme pode ser so o conteudo (sem as aspas) — decisao sua, documente.
     */
    private Token scanString() {
        // TODO (seu): implementar o AFD de string + caso de erro.
        throw new UnsupportedOperationException("scanString: implementar (Checkpoint 1)");
    }

    /**
     * TOKEN 4 + pontuacao — Operadores e delimitadores.
     *
     * ATENCAO AO LOOKAHEAD (use peek()/peekNext(), nunca consuma sem decidir):
     *   '*'  -> se o proximo for '*', e' POW (**); senao STAR
     *   '='  -> se o proximo for '=', e' EQ_EQ (==); senao ASSIGN
     *   '!'  -> se o proximo for '=', e' NEQ (!=); senao NOT
     *   '<'  -> '<=' LE senao LT ;   '>' -> '>=' GE senao GT
     *   '&'  -> exige '&&' (AND) ;   '|' -> exige '||' (OR)
     *   um-char diretos: + - / % ( ) { } ; ,
     *   qualquer outro caractere -> LexicalError("caractere inesperado")
     */
    private Token scanOperatorOrPunctuation() {
        // TODO (seu): implementar o despacho de operadores/pontuacao com lookahead.
        throw new UnsupportedOperationException("scanOperatorOrPunctuation: implementar (Checkpoint 1)");
    }

    // ------------------------------------------------------------------
    //  CLASSES DE CARACTERE (helpers — pode usar a vontade)
    // ------------------------------------------------------------------
    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
