# Projeto da AST — MiniLang

Checkpoint 3 · Paulo Borges · Construção de Compiladores · UFU

Projeto feito no papel antes do código (Seção 2 do enunciado). Critério da Aula 7: **um tipo de nó por categoria sintática que importa para as fases seguintes**, e não um por não-terminal da gramática (padrão Interpreter: categoria → classe abstrata; forma → subclasse; símbolos do lado direito → atributos).

---

## 1. Hierarquia de nós

<!-- TODO: uma linha por nó. Categorias (classes abstratas) primeiro, depois os nós concretos de cada uma.
     Cobrir no mínimo o escopo fixo (Seção 9 do enunciado): programa; declarações de variável, de procedimento
     e de função, com parâmetros; tipos; comandos (bloco, atribuição, seleção, repetição, chamada, retorno);
     expressões (literais dos cinco tipos básicos, identificador, binária, unária, chamada de função).
     Todo nó herda de Node a linha e a coluna (Seção 4 deste documento). -->

| Nó | Estende | Atributos (tipo) | Produção(ões) da gramática do CP2 que o geram |
|---|---|---|---|
| `Node` | — | `line: int`, `col: int` | (raiz da hierarquia) |
| <!-- TODO --> | | | |

---

## 2. Da gramática à árvore

Para cada não-terminal da gramática do CP2 (`gramatica-first-follow.md`, seção 1.3): vira um nó (qual), vira atributo de outro nó, ou não vira nada (com justificativa de uma frase).

| Não-terminal | Vira | Justificativa |
|---|---|---|
| <!-- TODO --> | nó `X` / atributo `y` de `X` / nada | |

### 2.1 Os três casos da Aula 7

**(a) Não-terminais criados para a precedência**
<!-- TODO: quais são (ExpOR … ExpUnaria) e por que viram um único tipo de expressão. -->

**(b) Não-terminais criados para eliminar a recursão à esquerda**
<!-- TODO: quais são (as caudas …Resto) e como o parser monta o nó sem eles (acumulador ou laço). -->

**(c) Não-terminais criados pela fatoração**
<!-- TODO: quais são (DeclResto, CmdIdResto, FatorIdResto …) e onde a informação deles vai parar. -->

**Pontuação e delimitadores**
<!-- TODO: parênteses, vírgulas, ponto e vírgula e chaves não viram nó. Dizer por quê, em uma frase. -->

---

## 3. Decisões de representação

<!-- TODO: escolhas que não vêm da gramática. Uma linha por decisão, objetiva. Exemplos do que precisa estar decidido:
     - como o if com elif/else é representado;
     - se o tipo é um nó ou um valor (enum) dentro do nó;
     - chamada como comando × chamada como expressão;
     - const × variável;
     - como os literais guardam o valor;
     - como a expressão isolada é testada (ver AstTest.arvoreDaExpressao). -->

---

## 4. Posição

<!-- TODO: de qual token cada nó tira a linha/coluna (ex.: binária = posição do operador ou do operando esquerdo?). -->

---

## 5. Campos para as próximas fases

Na aula de Tabela de Símbolos, cada identificador vai apontar para o seu símbolo; na Análise Semântica, cada expressão vai carregar o seu tipo. Nada disso é implementado agora.

<!-- TODO: em que classes esses campos vão entrar depois, e por que isso não muda a forma da árvore. -->

---

## 6. Formato do impressor de árvore

<!-- TODO: regras do formato (um nó por linha, indentação por profundidade, atributos ao lado do nome),
     de modo determinístico e suficiente para distinguir duas árvores diferentes. Incluir um exemplo
     (o do enunciado é a - b - c * d). -->

---

## 7. Forma canônica do reimpressor

<!-- TODO: regras da reimpressão (parênteses em toda expressão binária? indentação? uma declaração por linha?)
     e por que o texto produzido volta a ser aceito pelo parser. -->

---

## 8. Árvore desenhada à mão

Programa curto da MiniLang, com pelo menos uma função, uma seleção e uma repetição, e uma árvore com no mínimo 15 nós.

```
TODO: programa
```

```
TODO: árvore (desenho em texto ou link para a foto em uma pasta deste checkpoint)
```

**Três pedaços da árvore de derivação que a AST descartou**

1. <!-- TODO (ex.: a cadeia Expressao → ExpOR → … → Fator → ID que virou um único nó de identificador) -->
2. <!-- TODO -->
3. <!-- TODO -->

---

## 9. O segundo visitor exigiu mudar algum nó?

<!-- TODO: "não" ou o que faltava no desenho original e o que mudou (Seção 3.2 do enunciado). -->
