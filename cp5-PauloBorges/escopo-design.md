# Escopo e tabela de símbolos — MiniLang

Checkpoint 4 · Paulo Borges · Construção de Compiladores · UFU

---

## 1. Ambiente em cada ponto do programa

Programa curto demonstrando o comportamento da pilha de escopos (tabelas de símbolos):

```minilang
1: int x = 10;
2: 
3: int testar(int a) {
4:     int y = a;
5:     if (y > 0) {
6:         int x = 20;
7:         print(x);
8:     }
9:     print(x);
10:    return y;
11: }
```

**Tabela de resolução e estado do ambiente**

| Linha | Evento | Ambiente depois | Usos resolvidos |
|---|---|---|---|
| 1 | Declaração global de `x` | `[{x@1}]` | - |
| 3 | Declaração da função `testar` | `[{x@1, testar@3}]` | - |
| 3 | Abertura do escopo da função e inserção do param `a` | `[{x@1, testar@3}, {a@3}]` | - |
| 4 | Resolve o uso, depois declara local `y` | `[{x@1, testar@3}, {a@3, y@4}]` | `a` -> `a@3` |
| 5 | Resolve o uso, depois abre o bloco do `if` | `[{x@1, testar@3}, {a@3, y@4}, {}]` | `y` -> `y@4` |
| 6 | Declaração local de `x` (sombreamento) | `[{x@1, testar@3}, {a@3, y@4}, {x@6}]` | - |
| 7 | Leitura de `x` no `print` interno | (inalterado) | `x` -> `x@6` |
| 8 | Fechamento do bloco do `if` (desempilha) | `[{x@1, testar@3}, {a@3, y@4}]` | - |
| 9 | Leitura de `x` no `print` externo | (inalterado) | `x` -> `x@1` |
| 10| `return y` | (inalterado) | `y` -> `y@4` |
| 11| Fechamento do corpo da função (desempilha) | `[{x@1, testar@3}]` | - |

---

## 2. O que o `Symbol` guarda

| Campo | Tipo | Fase que pergunta | Por quê |
|---|---|---|---|
| `name` | `String` | Resolução, Semântica | Usado como chave principal na tabela de símbolos e para exibir o nome da variável/função nas mensagens de erro. |
| `category` | `enum SymbolCategory` | Semântica | Enum com `VAR`, `CONST`, `PARAM`, `FUNC` e `PROC`. Diferencia regras de uso (ex.: proibir atribuição a `CONST` ou `FUNC`, proibir uso de `PROC` em expressões). |
| `declNode` | `Node` (nó da AST) | Resolução, Semântica | Guarda a referência cruzada para o nó da declaração na AST. Permite buscar a linha/coluna para o relatório de erros e ler propriedades extras (como o `isConst`, bloco interno ou a lista de parâmetros de uma função). |
| `level` | `int` | Semântica, Ger. de Código | Nível de aninhamento (0 = global, 1+ = local). Na geração de código, decide se a variável será acessada na memória global ou na pilha de execução. |

**Tipo e deslocamento.** `type` e `offset` já existem na classe, mas ficam fora do construtor: começam com `null` e `-1` e são preenchidos pelas próximas fases (tipo no CP5, deslocamento na geração de código). A passada de identificação não mexe neles, e a classe não muda de forma quando essas fases chegarem.

**Constantes.** `const` não precisou de campo extra: a categoria `CONST` impede atribuição, e o valor está no `init` do `VarDecl`, alcançado por `declNode`.

**Onde fica o `decl`.** No CP3, só o `IdExpr` virou nó próprio; nos outros usos, o nome ficou como `String id` dentro do comando. Por isso o campo `Symbol decl` (começa `null`) entra em cinco nós:

- `IdExpr`: uso em expressão;
- `AssignCmd`: alvo da atribuição;
- `ReadCmd`: alvo do `read`;
- `CallCmd` e `CallExpr`: nome chamado.

---

## 3. Implementação da tabela

Pilha de mapas: um `HashMap<String, Symbol>` por escopo. Entrar num bloco empilha um mapa e sair desempilha. `lookup` procura do topo para a base e `lookupLocal` olha só o topo. A escolha acompanha a travessia do Visitor, que já abre e fecha blocos nessa ordem.

O construtor já abre o escopo global (`level() == 0`). Um `closeScope` que tentaria remover o global lança `IllegalStateException("closeScope sem escopo aberto")`. A passada sempre chama `openScope` e `closeScope` em pares, então isso só acontece por erro no código do compilador, nunca por causa do programa analisado.

## 4. Decisões de projeto

| Decisão | Escolha | Alternativa descartada | Motivo | Programa de teste |
|---|---|---|---|---|
| Sombreamento | Um nome interno pode ocultar um externo. Redeclarar no mesmo escopo é sempre erro. | Proibir qualquer sombreamento. | É o comportamento de C e Java: nomes curtos como `i` e `x` podem ser reusados em blocos internos sem conflito com os de fora. | `decisao-sombreamento.ml` |
| Declarar antes de usar | Obrigatório. O inicializador é resolvido antes de o nome ser declarado: em `int x = x;`, o `x` da direita é o externo ou, no global, "não declarado". | Permitir uso antes da declaração com uma passada prévia. | Uma passada só, de cima para baixo, na mesma ordem em que o programa é lido. | `decisao-declarar-antes.ml` |
| Funções mutuamente recursivas | Não permitidas: a primeira função a chamar a outra encontra um nome ainda não declarado. A recursão direta funciona, porque o nome é declarado antes do corpo. | *Forward declaration* ou passada prévia dos nomes globais. | *Forward declaration* exigiria mudar a gramática; a passada prévia exigiria uma segunda travessia das declarações globais, ou seja, mais um visitor. Com "declarar antes de usar", uma passada basta. | `decisao-recursao-mutua.ml` |
| Parâmetros e corpo | Mesmo escopo: o bloco do corpo não abre um escopo novo. | O corpo abrir um escopo próprio, dentro do escopo dos parâmetros. | Uma variável local não pode esconder um parâmetro sem aviso: `void f(int a) { int a; }` é redeclaração. | `decisao-parametros-corpo.ml` |
| Espaços de nomes | Um só espaço para variáveis, funções e procedimentos. | Um espaço para variáveis e outro para funções e procedimentos. | Um mapa por escopo basta, e um nome não pode significar duas coisas no mesmo nível: `int f = 1; void f() { }` é redeclaração. Com sombreamento, uma variável local pode ocultar uma função; aí uma chamada `f()` resolve para a variável, e quem acusa é o CP5 ("não é função"). | `decisao-espaco-nomes.ml` |

---

## 5. Formato da saída da passada

**Erros**, um por linha, na ordem em que aparecem no texto:
- `linha X: 'nome' já declarado neste escopo (linha Y)`
- `linha X: 'nome' não declarado`

**Resoluções**, uma por uso que achou declaração, na ordem do texto, no formato `nome@linhaDoUso -> nome@linhaDaDeclaração`.

Um uso não declarado não aparece nas resoluções, apenas na lista de erros. Nos arquivos de saída `test/esperado/`, as resoluções são impressas primeiro e os erros depois.

Exemplo de saída para o caso de sombreamento e redeclaração:
```
x@6 -> x@5
linha 7: 'x' já declarado neste escopo (linha 5)
```
