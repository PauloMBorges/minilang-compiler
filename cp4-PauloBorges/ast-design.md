# Projeto da AST — MiniLang

Checkpoint 3 · Paulo Borges · Construção de Compiladores · UFU

---

## 1. Hierarquia de nós

| Nó | Estende | Atributos (tipo) | Produção(ões) exatas da gramática que o geram |
|---|---|---|---|
| `Node` | — | `line: int`, `col: int` | (raiz da hierarquia) |
| `Decl` | `Node` | — | (classe abstrata para declarações) |
| `Cmd` | `Node` | — | (classe abstrata para comandos) |
| `Expr` | `Node` | — | (classe abstrata para expressões) |
| `Type` | `Node` | — | (classe abstrata para tipos) |
| `Program` | `Node` | `decls: List<Decl>` | `Programa ::= ListaDeclaracoes` |
| `VarDecl` | `Decl` | `isConst: boolean`, `type: Type`, `id: String`, `init: Expr` *(anulável)* | `DeclConst ::= "const" Tipo ID "=" Expressao ";"` <br> `DeclVarFuncao ::= Tipo ID DeclResto` (onde `DeclResto ::= AtribuicaoOpcional ";"`) <br> `DeclVarLocal ::= Tipo ID AtribuicaoOpcional ";"` |
| `FuncDecl` | `Decl` | `type: Type`, `id: String`, `params: List<Param>`, `body: BlockCmd` | `DeclVarFuncao ::= Tipo ID DeclResto` (onde `DeclResto ::= "(" OpcionalParams ")" Bloco`) |
| `ProcDecl` | `Decl` | `id: String`, `params: List<Param>`, `body: BlockCmd` | `DeclVoidFuncao ::= "void" ID "(" OpcionalParams ")" Bloco` |
| `Param` | `Node` | `type: Type`, `id: String` | `ListaParams ::= Tipo ID ListaParamsResto` |
| `PrimitiveType`| `Type` | `name: String` | `Tipo ::= "int" \| "double" \| "bool" \| "char" \| "string"` |
| `BlockCmd` | `Cmd` | `cmds: List<Cmd>` | `Bloco ::= "{" ListaComandos "}"` |
| `DeclCmd` | `Cmd` | `decl: VarDecl` | `Comando ::= DeclConst \| DeclVarLocal` |
| `AssignCmd` | `Cmd` | `id: String`, `expr: Expr` | `CmdId ::= ID CmdIdResto`<br>`CmdIdResto ::= "=" Expressao ";"` |
| `IfCmd` | `Cmd` | `cond: Expr`, `thenCmd: BlockCmd`, `elifs: List<ElifBranch>`, `elseCmd: BlockCmd` *(anulável)* | `CmdIf ::= "if" "(" Expressao ")" Bloco ListaElif ElseOpcional` |
| `ElifBranch` | `Node` | `cond: Expr`, `block: BlockCmd` | `ListaElif ::= "elif" "(" Expressao ")" Bloco ListaElif \| ε` |
| `WhileCmd` | `Cmd` | `cond: Expr`, `body: BlockCmd` | `CmdWhile ::= "while" "(" Expressao ")" Bloco` |
| `CallCmd` | `Cmd` | `id: String`, `args: List<Expr>` | `CmdId ::= ID CmdIdResto`<br>`CmdIdResto ::= "(" OpcionalArgs ")" ";"` |
| `ReturnCmd` | `Cmd` | `expr: Expr` *(anulável)* | `CmdReturn ::= "return" RetornoOpcional ";"` |
| `PrintCmd` | `Cmd` | `expr: Expr` | `CmdPrint ::= "print" "(" Expressao ")" ";"` |
| `ReadCmd` | `Cmd` | `id: String` | `CmdRead ::= "read" "(" ID ")" ";"` |
| `IntLiteral` | `Expr` | `value: long`, `lexeme: String` | `Fator ::= INT_LIT` |
| `DoubleLiteral`| `Expr` | `value: double`, `lexeme: String` | `Fator ::= DOUBLE_LIT` |
| `BoolLiteral` | `Expr` | `value: boolean`, `lexeme: String` | `Fator ::= "true" \| "false"` |
| `CharLiteral` | `Expr` | `value: char`, `lexeme: String` | `Fator ::= CHAR_LIT` |
| `StringLiteral`| `Expr` | `value: String`, `lexeme: String` | `Fator ::= STRING_LIT` |
| `IdExpr` | `Expr` | `id: String` | `Fator ::= ID FatorIdResto` (onde `FatorIdResto ::= ε`) |
| `BinExpr` | `Expr` | `left: Expr`, `op: String`, `right: Expr` | `ExpORResto ::= "\|\|" ExpAND ExpORResto` <br> `ExpANDResto ::= "&&" ExpIgualdade ExpANDResto` <br> `ExpIgualResto ::= ("==" \| "!=") ExpRelacional ExpIgualResto` <br> `ExpRelResto ::= ("<" \| ">" \| "<=" \| ">=") ExpAdd ExpRelResto` <br> `ExpAddResto ::= ("+" \| "-") ExpMult ExpAddResto` <br> `ExpMultResto ::= ("*" \| "/" \| "%") ExpPot ExpMultResto` <br> `ExpPotResto ::= "**" ExpPot` |
| `UnExpr` | `Expr` | `op: String`, `expr: Expr` | `ExpUnaria ::= ("-" \| "!") ExpUnaria` |
| `CallExpr` | `Expr` | `id: String`, `args: List<Expr>` | `Fator ::= ID FatorIdResto` (onde `FatorIdResto ::= "(" OpcionalArgs ")"`)|
---

## 2. Da gramática à árvore

| Não-terminal | Vira | Justificativa |
|---|---|---|
| `Programa` | Nó `Program` | |
| `ListaDeclaracoes` | Atributo `decls` de `Program` | |
| `Declaracao` | Nó abstrato `Decl` | |
| `DeclConst` | Nó `VarDecl` (isConst = true) | |
| `DeclVarFuncao` | Nó `VarDecl` ou `FuncDecl` | |
| `DeclResto` | Nada | Apenas direciona sintaticamente o parser para criar um `VarDecl` ou `FuncDecl`. |
| `DeclVoidFuncao` | Nó `ProcDecl` | |
| `AtribuicaoOpcional` | Atributo `init` de `VarDecl` | |
| `OpcionalParams` | Atributo `params` de func/proc | |
| `Tipo` | Nó `PrimitiveType` | |
| `ListaParams` | Nó `Param` (inicia a lista `params`) | |
| `ListaParamsResto` | Nada | O laço acrescenta cada item à lista params. |
| `Bloco` | Nó `BlockCmd` | |
| `ListaComandos` | Atributo `cmds` de `BlockCmd` | |
| `Comando` | Nó abstrato `Cmd` (nas decls, embrulha o `VarDecl` num `DeclCmd`) | |
| `DeclVarLocal` | Nó `VarDecl` (isConst = false) | |
| `CmdId` | Nó `AssignCmd` ou `CallCmd` | |
| `CmdIdResto` | Nada | Define no parser qual dos comandos acima será construído. |
| `OpcionalArgs` | Atributo `args` | |
| `ListaArgs` | Atributo `args` (inicia a lista) | |
| `ListaArgsResto` | Nada | O laço acrescenta cada item à lista args. |
| `CmdIf` | Nó `IfCmd` | |
| `ListaElif` | Nó `ElifBranch` (adicionado à lista `elifs`) | |
| `ElseOpcional` | Atributo `elseCmd` de `IfCmd` | |
| `CmdWhile` | Nó `WhileCmd` | |
| `CmdReturn` | Nó `ReturnCmd` | |
| `RetornoOpcional` | Atributo `expr` de `ReturnCmd` | |
| `CmdPrint` | Nó `PrintCmd` | |
| `CmdRead` | Nó `ReadCmd` | |
| `Expressao` | Nada | Apenas delega a chamada inicial para a hierarquia de `ExpOR`. |
| `ExpOR` a `ExpMult` | Nada (passagem direta) | Propagam o nó inferido para cima ou viram a raiz de um novo `BinExpr` se a cauda existir. |
| `ExpORResto` a `ExpMultResto`| Nada | Cada volta do laço cria um BinExpr, e o operador vira op. |
| `ExpPot` | Nada (passagem direta) | Segue a regra acima, atuando no topo da recursão da potência. |
| `ExpPotResto` | Nada | Aciona a chamada recursiva (associativa à direita) fornecendo o `**` para o novo BinExpr. |
| `ExpUnaria` | Nó `UnExpr` ou Nada | Só vira `UnExpr` na alternativa com operador; na alternativa `Fator`, não cria nó e repassa o que o `Fator` devolveu. |
| `Fator` | `IdExpr`, `CallExpr`, nó de literal ou Nada | `ID` sozinho vira `IdExpr`; `ID (...)` vira `CallExpr`; literais viram seus nós; em `( Expressao )` não gera nó, devolve a expressão de dentro direto. |
| `FatorIdResto` | Nada | Direciona o parser para criar um `CallExpr` se encontrar parênteses, senão `IdExpr`. |

### 2.1 Os três casos da Aula 7

**(a) Não-terminais criados para a precedência**
Os não-terminais das camadas de expressão (`ExpOR`, `ExpAND`, `ExpIgualdade`, `ExpRelacional`, `ExpAdd`, `ExpMult`, `ExpPot`, `ExpUnaria`, `Fator`) existem na gramática apenas para guiar o parser sintaticamente pela ordem de avaliação correta, descendo do operador mais fraco para o mais forte. Na AST, todos eles "somem" em favor das classes abstratas e concretas (`BinExpr`, `UnExpr` ou literais), pois a própria topologia da árvore (onde o operador de maior precedência fica mais profundo como folha/filho) já codifica a precedência de execução sem a necessidade de nós sintáticos extras.

**(b) Não-terminais criados para eliminar a recursão à esquerda**
Os não-terminais de cauda de expressão (`ExpORResto` a `ExpMultResto`) foram gerados para a gramática se tornar LL(1). Na árvore abstrata, eles não existem como nós. O parser implementa as caudas iterativas transformando-as em um laço `while`; a cada iteração de um operador iterativo associativo à esquerda (como `+` ou `||`), o parser cria um novo `BinExpr` usando a árvore construída até ali como filho esquerdo, lê o novo operador, o filho direito, e faz a nova árvore engolir a antiga, acumulando o resultado iterativamente sem criar nós "Resto".

**(c) Não-terminais criados pela fatoração**
Os não-terminais criados pelo agrupamento de prefixos comuns (`DeclResto`, `CmdIdResto`, `FatorIdResto` e `ExpPotResto`) serviram para resolver o conflito de prefixo comum (alternativas com FIRST iguais). Eles não vão parar na árvore como nós; suas informações direcionam os comandos de controle de fluxo do parser a instanciar o nó concreto final definitivo. No caso de `ExpPotResto`, que veio da fatoração do prefixo comum `ExpUnaria`, como a cauda chama `ExpPot` de novo, o parser monta o `BinExpr("**")` com o resultado da chamada recursiva como filho direito; isso mantém a associatividade à direita: `a ** b ** c` vira `a ** (b ** c)`.

**Não-terminais criados pela conversão EBNF → BNF**
Não-terminais criados para traduzir opcionalidades e repetições (`ListaDeclaracoes`, `ListaComandos`, `OpcionalParams`, `OpcionalArgs`, `ListaParamsResto`, `ListaArgsResto`, `ListaElif`, `AtribuicaoOpcional`, `ElseOpcional`, `RetornoOpcional`) viram atributos `List` ou atributos anuláveis, como `init`, `elseCmd` e o `expr` do `ReturnCmd`.

**Pontuação e delimitadores**
Símbolos como parênteses `()`, chaves `{}`, vírgulas `,` e ponto e vírgula `;` são organizacionais/sintáticos na gramática e não viram nós na AST porque a estruturação em árvore (quem é pai de quem, ou itens dentro de uma mesma `List`) já garante os limites semânticos de blocos, agrupamentos matemáticos e listas de argumentos.

---

## 3. Decisões de representação

- **If com elif/else:** Representado de forma plana no nó `IfCmd` através de uma lista de nós `ElifBranch` (evitando o aninhamento profundo na árvore) e um atributo opcional para o bloco do `else` (`elseCmd`).
- **Tipo como nó:** `PrimitiveType` é um nó da AST (herda de `Type` -> `Node`) em vez de um simples valor `enum`. Isso permite que o tipo tenha atributos de linha/coluna e seja processado diretamente pelo padrão *Visitor*.
- **Chamada como comando × chamada como expressão:** O conceito foi separado em dois nós distintos (`CallCmd` e `CallExpr`). Apesar de possuírem a mesma estrutura interna (ID e argumentos), isso garante o respeito estrito à hierarquia de classes, onde comandos não se misturam com expressões.
- **Const × variável:** Ambas usam o mesmo nó `VarDecl`, sendo diferenciadas apenas pelo atributo `isConst: boolean`.
- **Declarações locais em blocos:** Como `VarDecl` herda de `Decl` e o bloco espera uma lista de `Cmd`, as variáveis e constantes locais são embrulhadas em um nó adaptador `DeclCmd`.
- **Literais e seus valores:** Além do valor convertido (`long`, `double`, `boolean`, etc.), os nós literais guardam a `String` bruta do `lexeme` original (preservando sublinhados `_`).
- **Tratamento de exceções na conversão numérica:** Para evitar que o *parser* quebre com `NumberFormatException`, os `_` são removidos da string antes da conversão. Valores inteiros são armazenados em `long` no `IntLiteral` para postergar o erro de estouro de `int` para a análise semântica. Caso o número seja absurdamente grande e não caiba nem em um `long`, o parser captura a exceção, registra um erro na posição do literal e salva o valor como `0L` para prosseguir com a análise. O `parseDouble` não sofre desse problema, pois estouros viram `Infinity` nativamente.
- **Identificadores (Tabela de Símbolos):** Nós como `AssignCmd`, `CallCmd` e `ReadCmd` armazenam o identificador alvo diretamente como uma `String id` (e futuramente receberão seu próprio campo de referência à tabela de símbolos), em vez de usar um `IdExpr` encapsulado.
- **Teste da expressão isolada:** Para o teste da expressão isolada (`AstTest.arvoreDaExpressao`), a expressão é embrulhada em um programa mínimo válido como uma string (ex: `void main() { print(<expr>); }`). O teste invoca o parser normalmente e então navega na árvore gerada (usando *casts* restritos ao código de teste) até extrair e imprimir apenas a subárvore da expressão. Isso evita ter que alterar a visibilidade do `parseExpressao()` e garante que o EOF seja tratado sem conflitos com o `FOLLOW_EXPRESSAO`.

---

## 4. Posição

Todo nó herda `line` e `col` da classe `Node`. A posição de cada nó é extraída do token que "inicia" ou define a sua construção principal:

- **Programa (`Program`):** Posição do primeiro token do arquivo (geralmente o início da primeira declaração) ou linha 1, coluna 1 se estiver vazio.
- **Declarações (`VarDecl`, `FuncDecl`):** Token do modificador ou tipo que inicia a declaração (`const`, `int`, `double`, etc.).
- **Procedimento (`ProcDecl`):** Token da palavra reservada `"void"`.
- **Parâmetros (`Param`):** Token do tipo do parâmetro (`int`, `string`, etc.).
- **Tipo (`PrimitiveType`):** Token da própria palavra reservada do tipo.
- **Comandos de Controle e I/O (`IfCmd`, `WhileCmd`, `ReturnCmd`, `PrintCmd`, `ReadCmd`):** Token da palavra reservada correspondente (`if`, `while`, `return`, `print`, `read`).
- **Comandos de Bloco (`BlockCmd`):** Token de abertura do bloco (`{`).
- **Atribuições e Chamadas (`AssignCmd`, `CallCmd`, `CallExpr`):** Token do identificador (`ID`) sendo atribuído ou invocado.
- **Ramos Elif (`ElifBranch`):** Token da palavra reservada `"elif"`.
- **Declaração Local (`DeclCmd`):** Herda a posição da declaração interna (`VarDecl`) que ele embrulha.
- **Fatores (`IntLiteral`, `StringLiteral`, `IdExpr`, etc.):** O próprio token do literal ou o token do `ID`.
- **Expressão Unária (`UnExpr`):** Token do operador unário (`-` ou `!`).
- **Expressão Binária (`BinExpr`):** Posição do operando esquerdo, marcando onde a operação como um todo começa no código-fonte.

---

## 5. Formato do impressor de árvore

O impressor (implementado utilizando o padrão *Visitor*) percorre a Árvore Sintática Abstrata (AST) em pré-ordem e gera a saída textual seguindo regras estritas e determinísticas, projetadas para facilitar a validação dos arquivos esperados nos testes:

1. **Um nó por linha:** Cada instância de um nó ocupa exatamente uma linha.
2. **Indentação por profundidade:** A hierarquia é representada por 2 espaços de indentação adicionais para cada novo nível de profundidade (o nó raiz não possui espaços).
3. **Atributos simples na linha do nó:** Atributos de tipos simples (`String`, `boolean`, `int`, etc.) são impressos entre parênteses ao lado do nome do nó. 
   - Exemplos gerais: `VarDecl(isConst=true, id="PI")`, `FuncDecl(id="somar")`, `UnExpr(op="-")`, `PrimitiveType(name="int")`, `Param(id="a")` ou `ReadCmd(id="x")`.
   - **Literais:** Para garantir paridade exata com a entrada e não sofrer formatações automáticas do Java (como `1.0E10`), o `IntLiteral` e o `DoubleLiteral` exibem o atributo `lexeme` (ex: `IntLiteral(lexeme="10_000")`). Para textos, exibem o valor com as respectivas aspas: `StringLiteral(value="Compiladores")` e `CharLiteral(value='P')`. Já o `BoolLiteral` exibe seu valor lógico diretamente: `BoolLiteral(value=true)` ou `BoolLiteral(value=false)`.
   - **Posição:** Os atributos `line` e `col` **não** são impressos. Isso mantém a árvore limpa e focada na estrutura sintática.
4. **Nós e listas viram filhos:** Atributos que são outros nós ou listas de nós não são impressos entre parênteses; eles viram linhas filhas, impressas abaixo do pai com a indentação incrementada, respeitando a ordem declarada na classe. 
   - Exemplo: em um `VarDecl`, o `PrimitiveType` e o `Expr` do `init` aparecem nas linhas de baixo. Em um `FuncDecl`, o tipo de retorno, a sequência de `Param` e o `BlockCmd` aparecem como filhos.
5. **Filhos nulos e listas vazias são omitidos:** Se um nó filho for `null` (ex: `init` ausente, `elseCmd` ausente, `return;` sem expressão) ou se uma lista for vazia (parâmetros, argumentos, `elifs`), **nenhuma linha é gerada**. A árvore continua perfeitamente determinística e sem ambiguidade por conta dos tipos dos filhos. Por exemplo: em um `IfCmd`, o primeiro filho `BlockCmd` é obrigatoriamente o `thenCmd`; qualquer nó `ElifBranch` vem na sequência; se houver um segundo `BlockCmd` no final, é garantido que seja o `elseCmd`.

**Exemplo:**
Para a expressão `a - b - c * d`, como o operador `*` tem precedência maior sobre o `-`, e o operador `-` tem associatividade à esquerda, a árvore representa organicamente o agrupamento `(a - b) - (c * d)`. O impressor gera a seguinte saída:

```text
BinExpr(op="-")
  BinExpr(op="-")
    IdExpr(id="a")
    IdExpr(id="b")
  BinExpr(op="*")
    IdExpr(id="c")
    IdExpr(id="d")
```

---

## 6. Árvore desenhada à mão

1. Programa curto da MiniLang, com uma função, uma seleção (if) e uma repetição (while), gerando uma árvore com mais de 15 nós.

```
int contar(int n) {
    int i = 0;
    while (i < n) {
        if (i == 5) {
            print(i);
        }
        i = i + 1;
    }
    return i;
}
```
2. Árvore Sintática Abstrata (AST):
```
Program
  FuncDecl(id="contar")
    PrimitiveType(name="int")
    Param(id="n")
      PrimitiveType(name="int")
    BlockCmd
      DeclCmd
        VarDecl(isConst=false, id="i")
          PrimitiveType(name="int")
          IntLiteral(lexeme="0")
      WhileCmd
        BinExpr(op="<")
          IdExpr(id="i")
          IdExpr(id="n")
        BlockCmd
          IfCmd
            BinExpr(op="==")
              IdExpr(id="i")
              IntLiteral(lexeme="5")
            BlockCmd
              PrintCmd
                IdExpr(id="i")
          AssignCmd(id="i")
            BinExpr(op="+")
              IdExpr(id="i")
              IntLiteral(lexeme="1")
      ReturnCmd
        IdExpr(id="i")
```

3. Três pedaços da árvore de derivação que a AST descartou:
     1. A cadeia de precedência de expressões (Expressao → ExpOR → ... → Fator → INT_LIT): Na árvore de derivação, o valor 0 desce por todos os não-terminais de precedência. Na AST, todos esses nós sintáticos foram descartados e substituídos diretamente por um único nó folha IntLiteral(lexeme="0")
     2. Os símbolos de pontuação e delimitadores (como os parênteses em (i < n) ou as chaves { }): Eles foram consumidos pelo parser por meio de chamadas match(), mas não viraram nós na AST, pois a própria hierarquia de pai e filho da árvore (WhileCmd contendo a condição e o BlockCmd) já garante o escopo e o agrupamento.
     3. O não-terminal de fatoração CmdIdResto na atribuição: Na derivação, para i = i + 1;, a gramática derivou Comando → CmdId → ID CmdIdResto. Na AST, o CmdId e o CmdIdResto sumiram; a constatação do sinal = serviu apenas para o parser decidir instanciar imediatamente um único nó concreto AssignCmd.
