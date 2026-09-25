# Especificação Léxica 

Sintaxe geral no estilo C/Java. Identificadores case-sensitive.

---

## 1. Tabela das categorias de token

| Categoria | Notação (regex/EBNF) | Exemplos válidos | Token(s) |
|---|---|---|---|
| **Identificador** | `(letra \| "_") (letra \| dígito \| "_")*` | `total`, `x1`, `taxa_juros`, `_tmp` | `ID` |
| **Palavra reservada** | mesmo padrão do identificador + busca em tabela | `if`, `while`, `int`, `return` | ver §6 |
| **Literal inteiro** | `dígito (dígito \| "_")*`  - `_` só entre dígitos | `42`, `0`, `1_000_000` | `INT_LIT` |
| **Literal real** | `dígito (dígito \| "_")* ( "." dígito (dígito \| "_")* expoente? \| expoente )` -   `_` só entre dígitos | `3.14`, `0.5`, `6.02e23`, `1e-10` | `DOUBLE_LIT` |
| **Literal string** | `'"' (car ≠ '"' ∧ car ≠ '\n')* '"'` | `"ok"`, `"linha 1"` | `STRING_LIT` |
| **Literal char** | `"'" (car ≠ "'" ∧ car ≠ '\n') "'"` | `'a'`, `'P'` | `CHAR_LIT` |
| **Operador** | 1 ou 2 caracteres (ver §7) | `=`, `==`, `<=`, `+`, `&&`, `**` | ver §7 |
| **Pontuação** | 1 caractere | `(` `)` `{` `}` `;` `,` | ver §7 |

Onde: `letra = [a-zA-Z]`, `dígito = [0-9]`, `expoente = ("e" | "E") ("+" | "-")? dígito (dígito \| "_")*`

---

## 2. Alfabeto de entrada

- **Letras** (identificadores/reservadas): `a-z`, `A-Z`.
- **Dígitos** (literais): `0-9`.
- **Símbolos e pontuação**: `_`, `"`, `'`, `.`, `,`, `;`, `(`, `)`, `{`, `}`
- **Operadores e lógicos**: `+`, `-`, `*`, `/`, `%`, `=`, `<`, `>`, `!`, `&`, `|`
- **Controle e formatação**: `Espaços em branco`, `\t`, `\r`, `\n`
- **Comentários**: `#`

Caracteres fora deste conjunto (por exemplo, letras acentuadas como `ç`, `ã`)
não fazem parte do alfabeto e, se encontrados fora de um comentário,
produzem um erro léxico de "caractere fora do alfabeto".

---

## 3. Sensibilidade a maiúsculas/minúsculas

A linguagem é **case-sensitive**: `Total`, `total` e `TOTAL` são três
identificadores distintos. Palavras reservadas e identificadores seguem a
mesma regra - as reservadas são reconhecidas exatamente na forma
minúscula listada em §6 (por exemplo, `While` não é a palavra-chave
`while`; é um identificador comum).

---

## 4. Espaços em branco e comentários

- **Espaços em branco** (espaço, `\t`, `\r`, `\n`) separam tokens e são
  descartados; não geram token. Servem apenas para o scanner atualizar a
  posição (linha/coluna).
- **Comentário de linha:** inicia com `#` e vai até o fim da linha (`\n`
  exclusive). Todo o conteúdo é descartado.
- **Comentário de bloco:** **não existe** na linguagem.

---

## 5. Desambiguação de operadores compostos (maximal munch)

A regra é **maximal munch** (casamento mais longo): ao reconhecer um operador,
o scanner sempre consome o **maior prefixo válido** antes de decidir o token.
Na prática, ao ver um caractere que pode iniciar um operador composto, o scanner
*espia* (lookahead de 1, sem consumir) o próximo caractere:

- `=` seguido de `=` → `==` (`EQ_EQ`); senão `=` sozinho (`ASSIGN`)
- `*` seguido de `*` → `**` (`POW`); senão `*` sozinho (`STAR`)
- `!` seguido de `=` → `!=` (`NEQ`); senão `!` sozinho (`NOT`)
- `<` seguido de `=` → `<=` (`LE`); senão `<` sozinho (`LT`)
- `>` seguido de `=` → `>=` (`GE`); senão `>` sozinho (`GT`)
- `&` **exige** um segundo `&` → `&&` (`AND`); `&` isolado é erro léxico
- `|` **exige** um segundo `|` → `||` (`OR`); `|` isolado é erro léxico

O mesmo princípio garante que `while1` é **um** identificador (não `while` + `1`)
e que `==` é **um** token (não dois `=`).

---

## 6. Palavras reservadas (lista fechada)

A lista abaixo é **fechada**: qualquer identificador que não conste nela é um
`ID` comum. As reservadas são reconhecidas pelo AFD de identificador e
classificadas por consulta a uma tabela hash (`ReservedWords`).

| Categoria | Palavras |
|---|---|
| Tipos | `int`, `double`, `bool`, `char`, `string` |
| Valores booleanos | `true`, `false` |
| Controle | `if`, `elif`, `else`, `while` |
| Funções / imutabilidade | `return`, `const` |
| Entrada e saída | `print`, `read` |

Total: 15 palavras reservadas.

`elif` permite encadear condicionais com uma única palavra reservada, ao invés 
do aninhamento else if (dois tokens); isso simplifica tanto o léxico quanto a futura
gramática.  
`const` habilita a declaração de constantes, o que abre caminho para uma verificação 
semântica nas entregas seguintes (impedir reatribuição de um valor const).

---

## 7. Operadores e pontuação (lista completa)

**Operadores aritméticos:** `+` (`PLUS`), `-` (`MINUS`), `*` (`STAR`),
`/` (`SLASH`), `%` (`PERCENT`), `**` (`POW`).

**Operadores relacionais:** `==` (`EQ_EQ`), `!=` (`NEQ`), `<` (`LT`),
`<=` (`LE`), `>` (`GT`), `>=` (`GE`).

**Operadores lógicos:** `&&` (`AND`), `||` (`OR`), `!` (`NOT`).

**Atribuição:** `=` (`ASSIGN`).

**Pontuação/delimitadores:** `(` `)` (`LPAREN`/`RPAREN`),
`{` `}` (`LBRACE`/`RBRACE`), `;` (`SEMI`), `,` (`COMMA`).

---

## 8. Tratamento de erros léxicos

Erros léxicos **não** interrompem a tokenização. O scanner reporta cada erro
com **linha e coluna** e **segue** para o próximo token (recuperação),
continuando até o fim do arquivo. Os erros são coletados numa lista acessível
via `Scanner.getErrors()`.

São erros léxicos:

- **Caractere fora do alfabeto** (ex.: `@`): reportado; o caractere é
  consumido e a tokenização prossegue.
- **String não fechada** até o fim da linha (`\n`) ou até o fim do arquivo
  (EOF): a linguagem **não** permite strings multilinha.
- **Char malformado**: char vazio (`''`), char com mais de um caractere
  (`'ab'`) ou char não fechado até `\n`/EOF.
- **Literal numérico malformado**: `_` fora de posição entre dígitos
  (ex.: `1_`, `1__0`), ponto sem dígito seguinte (`3.`), expoente sem dígito
  (`1e`).

---

## 9. Observações

- **Escapes** (`\"`, `\n`, `\'`) **não** são suportadas em strings nem em chars. 
  Uma barra invertida é um caractere comum. Em uma string, qualquer aspa dupla (") 
  encontrada atuará diretamente como o fechamento do token. Se o programador escrever 
  \n no código, o scanner lerá isso como dois caracteres separados (uma barra invertida 
  e a letra 'n'). Se ele pressionar "Enter" no meio de uma string no arquivo fonte, o 
  scanner lançará um erro de "String não fechada antes da quebra de linha".
- **Literais não-decimais** (hexadecimal `0x1F`, binário `0b1010`) **não** são
  reconhecidos - apenas decimal, com `_` como separador e notação científica.

