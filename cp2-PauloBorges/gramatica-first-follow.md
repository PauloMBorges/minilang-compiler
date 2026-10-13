# Gramática, FIRST/FOLLOW e verificação LL(1) — MiniLang

Checkpoint 2 · Paulo Borges · Construção de Compiladores · UFU

## 0. Convenções

- Notação: <!-- TODO: BNF ou EBNF; dizer qual símbolo é usado para produção (`->` ou `::=`) -->
- Terminais escritos com os nomes do `TokenType` do CP1 (ex.: `ID`, `INT_LIT`, `SEMI`, `LBRACE`), para que cada terminal da gramática corresponda exatamente a um `match(TokenType.X)` no parser.
- `ε` = cadeia vazia; `$` = fim da entrada (`TokenType.EOF`).
- Símbolo inicial: <!-- TODO -->

---

## 1. Gramática (Seção 2.1)

### 1.1 Versão inicial

<!-- TODO: a primeira versão escrita, antes das transformações (pode estar em EBNF). -->

```
```

### 1.2 Transformações aplicadas

Recursão à esquerda (Aula 4) e fatoração à esquerda (Aula 5). Uma linha por correção.

| Não-terminal | Problema | Antes | Depois |
|---|---|---|---|
| <!-- TODO --> | recursão à esquerda / prefixo comum / conflito LL(1) | | |

### 1.3 Gramática final (BNF, LL(1))

<!-- TODO: versão final, já sem recursão à esquerda e fatorada. É esta que o Parser.java implementa. -->

```
```

### 1.4 Decisões de projeto da sintaxe

<!-- TODO: escolhas que não vêm da teoria, e sim do projeto da linguagem.
     Ex.: como um procedimento (sem valor de retorno) é declarado com os tokens do CP1;
     como if/elif/else se associam; se blocos { } são obrigatórios; precedência e associatividade dos operadores. -->
**Estrutura do programa**
- Um programa é uma sequência de itens globais: declarações de variável, de constante e de função, e comandos, em qualquer ordem. Não há main.
- Funções só são declaradas no nível global (sem funções aninhadas)
- Um bloco { ... }, contém declarações de variável.constante e comandos, e pode ser vazio.

**Declarações**
- Variável: um tipo (int, double, bool, char, string), um ID, inicialização opcional (= expr) e ;. Uma variável por declaração.
- Constante: const, tipo, ID, = expr obrigatório e ;. Vale no nível global e dentro de blocos
- Função: tipo de retorno, ID, ( parâmetros ) e bloco. O tipo de retorno é um dos 5 tipos ou void; void não é tipo de variável
- Procedimento = função com retorno void
- Parâmetros: lista de tipo ID separados por ,: pode ser vazia
- Variável x função: depois de tipo ID, o token ( indica função; = ou ; indicam variável (fatoração à esquerda)

**Comandos**
- Atribuição: ID = expr ;. A atribuição é comando (não expressão)
- Chamada como comando: ID ( argumentos ) ;. Argumentos: expressões separadas por ,; podem ser vazios
- Atribuição x chamada: depois do ID, o token = indica atribuição e ( indica chamada
- if ( expr ) bloco, seguindo de zero ou mais elif ( expr ) bloco e, opcionalmente, else bloco
- while ( expr ) bloco
- Parênteses na condição e chaves no bloco são obrigatórios em if, elif, else e while. Com o bloco sempre fechado por }, não existe dangling else
- print ( expr ) ; e read ( ID ) ;
- return ; ou return expr ;. Se o return combina com o tipo da função, isso é verificado na análise semântica.
- Não há comando vazio (; sozinho) nem bloco solto como comando

**Expressões** (da menor para maior precedência) 

| Nível | Operadores | Associatividade |
|---|---|---|
| 1 | `\|\|` | esquerda |
| 2 | `&&` | esquerda |
| 3 | `==` `!=` | esquerda |
| 4 | `<` `<=` `>` `>=` | esquerda |
| 5 | `+` `-` | esquerda |
| 6 | `*` `/` `%` | esquerda |
| 7 | `**` | direita |
| 8 | `-` `!` (unários, prefixos) | direita |
| 9 | fator: `( expr )`, `INT_LIT`, `DOUBLE_LIT`, `STRING_LIT`, `CHAR_LIT`, `true`, `false`, `ID`, chamada `ID ( argumentos )` | — |

**`void`**
- `void` não existia no scanner do CP1. Foi adicionada como palavra reservada (`TokenType.VOID`), porque o escopo fixo exige procedimentos. Única alteração no scanner do CP1; ver `especificacao-lexica.md`, §6.

---

## 2. FIRST e FOLLOW (Seção 2.2)

Gramática aumentada usada no cálculo: <!-- TODO: S' -> <símbolo inicial> $ -->

Não-terminais anuláveis (derivam ε): <!-- TODO -->

| Não-terminal | FIRST | FOLLOW |
|---|---|---|
| <!-- TODO --> | | |

---

## 3. Verificação da condição LL(1) (Seção 2.3)

Para cada não-terminal com mais de uma alternativa:
(a) FIRST das alternativas disjuntos entre si;
(b) no máximo uma alternativa anulável;
(c) se há alternativa anulável, FIRST das demais disjunto de FOLLOW do não-terminal.
Não-terminais com uma única produção não têm decisão a verificar.

### 3.1 <!-- TODO: Não-terminal -->

- Alternativas e FIRST de cada uma: <!-- TODO -->
- (a) <!-- TODO: interseção dos FIRST = ∅ ? -->
- (b) <!-- TODO -->
- (c) <!-- TODO: FIRST(demais) ∩ FOLLOW = ∅ ? -->
- Conclusão: <!-- ok / violação encontrada e correção aplicada -->

<!-- repetir o bloco 3.x para cada não-terminal com alternativas -->

### 3.n Violações encontradas e correções

<!-- TODO: conflitos achados na verificação e como foram resolvidos (mais fatoração, reformulação da produção...). -->

---

## 4. Da gramática ao código

Ajuda a apontar, para cada decisão do parser, de onde ela vem (objetivo da Seção 1 do enunciado).

| Não-terminal | Método | Decide a alternativa por | Aceita ε quando | Sincroniza em |
|---|---|---|---|---|
| <!-- TODO --> | `parseX()` | FIRST(...) | lookahead ∈ FOLLOW(...) | FOLLOW(...) + âncoras |
