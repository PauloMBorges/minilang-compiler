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
