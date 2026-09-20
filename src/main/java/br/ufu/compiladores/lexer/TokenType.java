package br.ufu.compiladores.lexer;

/* Categorias de token da MiniLang. */
public enum TokenType {

    // --- Identificadores ---
    ID,                 // (letra | '_') (letra | digito | '_')*

    // --- Palavras reservadas ---
    // Reconhecidas como ID e classificadas por tabela (ver ReservedWords).
    INT, BOOL, DOUBLE,          // tipos
    TRUE, FALSE,                // literais booleanos (tratados como reservadas)
    IF, ELIF, ELSE, WHILE,      // controle
    RETURN, CONST,              // funcoes / imutabilidade
    PRINT, READ,                // E/S

    // --- Literais ---
    INT_LIT,            // digito (digito | '_')*   com '_' apenas entre digitos
    DOUBLE_LIT,         // digito+ ('.' digito+)? (('e'|'E') ('+'|'-')? digito+)?
    STRING_LIT,         // " (car != ")* "

    // --- Operadores ---
    PLUS, MINUS, STAR, SLASH, PERCENT,   // +  -  *  /  %
    POW,                                 // **  (exige lookahead vs STAR)
    ASSIGN,                              // =   (exige lookahead vs EQ_EQ)
    EQ_EQ, NEQ, LT, LE, GT, GE,          // ==  !=  <  <=  >  >=
    AND, OR, NOT,                        // &&  ||  !

    // --- Pontuacao ---
    LPAREN, RPAREN, LBRACE, RBRACE,      // (  )  {  }
    SEMI, COMMA,                         // ;  ,

    // --- Especiais ---
    EOF                 // fim do arquivo
}
