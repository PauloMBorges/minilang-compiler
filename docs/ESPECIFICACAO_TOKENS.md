# Especificação de Tokens — MiniLang

Sintaxe estilo C/Java. Identificadores case-sensitive.

## Identificadores
- `ID` : `(letra | '_') (letra | digito | '_')*`  — ex.: `taxa_juros`, `x`

## Palavras reservadas (reconhecidas como ID + tabela hash)
`int` `bool` `double` `true` `false` `if` `elif` `else` `while` `return` `const` `print` `read`

## Literais
- `INT_LIT`    : `digito (digito | '_')*`  — `_` apenas entre dígitos. `1_`, `_1`, `1__0` = erro
- `DOUBLE_LIT` : `digito+ ('.' digito+)? (('e'|'E') ('+'|'-')? digito+)?`  — `3.` = erro
- `STRING_LIT` : `" (car != ")* "`  — string sem fechar = erro léxico

## Operadores
- `+ - * / %` → PLUS MINUS STAR SLASH PERCENT
- `**` → POW (lookahead vs STAR)
- `= ==` → ASSIGN / EQ_EQ (lookahead)
- `!= < <= > >=` → NEQ LT LE GT GE
- `&& || !` → AND OR NOT

## Pontuação
`( ) { } ; ,` → LPAREN RPAREN LBRACE RBRACE SEMI COMMA

## Ignorados
- espaços, tab, `\n`
- comentário de linha: `#` até o fim da linha

## Justificativas (preencher para a defesa)
- Por que `elif` e `const`: _______
- Por que comentário `#` (e não `//`): _______
- Por que `_` como separador em números: _______
- Por que `**` e `%`: _______
