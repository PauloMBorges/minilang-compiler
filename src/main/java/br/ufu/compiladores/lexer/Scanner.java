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
    //  MÉTODOS AUXILIARES DO SCANNER
    // ------------------------------------------------------------------

    /* Helper para consumir sequências de dígitos que podem conter '_' */
    /* Garante que não existam '_' consecutivos e que o bloco não termine com '_' */
    private void consumeDigits(int startLine, int startCol) {
        while (hasNext()) {
            char c = peek();
            if (isDigit(c)) {
                advance();
            } else if (c == '_') {
                if (peekNext() == '_') {
                    throw new LexicalError("Underscores consecutivos não são permitidos", startLine, startCol);
                }
                advance();
            } else {
                break;
            }
        }

        if (source.charAt(pos - 1) == '_') {
            throw new LexicalError("O caractere '_' deve estar estritamente entre dígitos", startLine, startCol);
        }
    }



    // ------------------------------------------------------------------
    //  AFDs 
    // ------------------------------------------------------------------

    private Token scanIdentifierOrKeyword() {

        // 1. Marca ponto de partida (para saber onde o token começou)
        int startPos = pos;
        int startLine = line;
        int startCol = col;

        // 2. Laço de consumo (maximal munch)
        while (hasNext()) {
            char c = peek();

            if (isLetter(c) || isDigit(c) || c == '_') {
                advance();
            } else {
                break;
            }
        }

        // 3. Recorta a string do texto original
        String lexeme = source.substring(startPos, pos);

        // 4. Classifica usando a tabela hash
        TokenType type = ReservedWords.classify(lexeme);

        // 5. Cria e devolve o novo objeto Token
        return new Token(type, lexeme, startLine, startCol);

    }

    private Token scanNumber() {
        
        int startPos = pos;
        int startLine = line;
        int startCol = col;

        boolean isDouble = false; // é inteiro até encontrar '.' ou 'e/E'


        // 1. Parte inteira
        consumeDigits(startLine, startCol);

        // 2. Parte fracionária
        if (peek() == '.') {
            isDouble = true;
            advance();

            if (!isDigit(peek())) {
                throw new LexicalError("Formato inválido: esperado dígito após o ponto", startLine, startCol);
            }

            consumeDigits(startLine, startCol);
        }

        // 3. Notação científica
        char c = peek();
        if (c == 'e' || c == 'E') {
            isDouble = true;
            advance();

            // sinal opcional depois do 'e'
            char sign = peek();
            if (sign == '+' || sign == '-') {
                advance();
            }

            // exige pelo menos um dígito depois do 'e' / sinal
            if (!isDigit(peek())) {
                throw new LexicalError("Formato inválido: esperado dígito no expoente", startLine, startCol);
            }

            consumeDigits(startLine, startCol);
        }

        String lexeme = source.substring(startPos, pos);

        if (lexeme.endsWith("_")) {
            throw new LexicalError("Formato numérico inválido: não pode terminar com '_'", startLine, startCol);
        }

        TokenType type = isDouble ? TokenType.DOUBLE_LIT : TokenType.INT_LIT;

        return new Token(type, lexeme, startLine, startCol);
    }


    private Token scanString() {

        int startPos = pos;
        int startLine = line;
        int startCol = col;

        // Consome a aspa de abertura
        advance();

        while (hasNext()) {
            char c = peek();

            // Erro: quebra de linha no meio da string
            if (c == '\n') {
                throw new LexicalError("String não fechada antes da quebra de linha", startLine, startCol);
            }

            // Fim da string
            if (c == '"') {
                break;
            }

            // Consome qualquer outro caractere da string
            advance();
        }

        // Erro: chegou ao fim do arquivo sem fechar aspas
        if (!hasNext()) {
            throw new LexicalError("Fim de arquivo alcançado; string não fechada", startLine, startCol);
        }

        // Consome aspa de fechamento
        advance();

        // Lexema inclui as aspas originais
        String lexeme = source.substring(startPos, pos);

        return new Token(TokenType.STRING_LIT, lexeme, startLine, startCol);
    }

    private Token scanOperatorOrPunctuation() {

        int startPos = pos;
        int startLine = line;
        int startCol = col;

        // Consome primeiro caractere (pontuação/operador)
        char c = advance();
        TokenType type = null;

        switch(c) {
            case '*':
                if (peek() == '*') {
                    advance(); 
                    type = TokenType.POW;
                } else {
                    type = TokenType.STAR;
                }
                break;
            case '=':
                if (peek() == '=') {
                    advance();
                    type = TokenType.EQ_EQ;
                } else {
                    type = TokenType.ASSIGN;
                }
                break;
            case '!':
                if (peek() == '=') {
                    advance();
                    type = TokenType.NEQ;
                } else {
                    type = TokenType.NOT;
                }
                break;
            case '<':
                if (peek() == '=') {
                    advance();
                    type = TokenType.LE;
                } else {
                    type = TokenType.LT;
                }
                break;
            case '>':
                if (peek() == '=') {
                    advance();
                    type = TokenType.GE;
                } else {
                    type = TokenType.GT;
                }
                break;
            
            case '&':
                if (peek() == '&') {
                    advance();
                    type = TokenType.AND;
                } else {
                    // Apenas & é erro léxico
                    throw new LexicalError("Caractere '&' isolado inválido, esperado '&&'", startLine, startCol);
                }
                break;
            case '|':
                if (peek() == '|') {
                    advance();
                    type = TokenType.OR;
                } else {
                    throw new LexicalError("Caractere '|' isolado inválido, esperado '||'", startLine, startCol);
                }
                break;
            
            case '+': type = TokenType.PLUS; break;
            case '-': type = TokenType.MINUS; break;
            case '/': type = TokenType.SLASH; break;
            case '%': type = TokenType.PERCENT; break;
            case '(': type = TokenType.LPAREN; break;
            case ')': type = TokenType.RPAREN; break;
            case '{': type = TokenType.LBRACE; break;
            case '}': type = TokenType.RBRACE; break;
            case ';': type = TokenType.SEMI; break;
            case ',': type = TokenType.COMMA; break;

            // Erro de caractere desconhecido
            default:
                throw new LexicalError("Caractere inesperado: '" + c + "'", startLine, startCol);
            
        }

        String lexeme = source.substring(startPos, pos);

        return new Token(type, lexeme, startLine, startCol);
    
    }

    // ------------------------------------------------------------------
    //  CLASSES DE CARACTERE 
    // ------------------------------------------------------------------
    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
