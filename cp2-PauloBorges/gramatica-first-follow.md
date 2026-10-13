# Gramática, FIRST/FOLLOW e verificação LL(1) — MiniLang

Checkpoint 2 · Paulo Borges · Construção de Compiladores · UFU

## 0. Convenções

- Notação: <!-- TODO: BNF ou EBNF; dizer qual símbolo é usado para produção (`->` ou `::=`) -->
- Terminais escritos com os nomes do `TokenType` do CP1 (ex.: `ID`, `INT_LIT`, `SEMI`, `LBRACE`), para que cada terminal da gramática corresponda exatamente a um `match(TokenType.X)` no parser.
- `ε` = cadeia vazia; `$` = fim da entrada (`TokenType.EOF`).
- Símbolo inicial: <!-- TODO -->

---

## 1. Gramática (Seção 2.1)

### 1.1 Versão inicial (EBNF)

```
// --- ESQUELETO E DECLARAÇÕES ---
Programa      ::= { Declaracao }
Declaracao    ::= DeclConst | DeclVar | DeclFuncao

DeclConst     ::= "const" Tipo ID "=" Expressao ";"
DeclVar       ::= Tipo ID [ "=" Expressao ] ";"
DeclFuncao    ::= TipoRetorno ID "(" [ ListaParams ] ")" Bloco

Tipo          ::= "int" | "double" | "bool" | "char" | "string"
TipoRetorno   ::= Tipo | "void"
ListaParams   ::= Tipo ID { "," Tipo ID }


// --- COMANDOS ---
Bloco         ::= "{" { Comando } "}"
Comando       ::= DeclConst
                | DeclVar 
                | Atribuicao 
                | ChamadaCmd 
                | CmdIf 
                | CmdWhile 
                | CmdReturn 
                | CmdPrint 
                | CmdRead

Atribuicao    ::= ID "=" Expressao ";"
ChamadaCmd    ::= ID "(" [ ListaArgs ] ")" ";"
CmdIf         ::= "if" "(" Expressao ")" Bloco { "elif" "(" Expressao ")" Bloco } [ "else" Bloco ]
CmdWhile      ::= "while" "(" Expressao ")" Bloco
CmdReturn     ::= "return" [ Expressao ] ";"
CmdPrint      ::= "print" "(" Expressao ")" ";"
CmdRead       ::= "read" "(" ID ")" ";"

ListaArgs     ::= Expressao { "," Expressao }

// --- EXPRESSÕES (Camadas de Precedência) ---
Expressao     ::= ExpOR

ExpOR         ::= ExpOR "||" ExpAND 
                | ExpAND

ExpAND        ::= ExpAND "&&" ExpIgualdade 
                | ExpIgualdade

ExpIgualdade  ::= ExpIgualdade ("==" | "!=") ExpRelacional 
                | ExpRelacional

ExpRelacional ::= ExpRelacional ("<" | ">" | "<=" | ">=") ExpAdd 
                | ExpAdd

ExpAdd        ::= ExpAdd ("+" | "-") ExpMult 
                | ExpMult

ExpMult       ::= ExpMult ("*" | "/" | "%") ExpPot 
                | ExpPot

// Associatividade à direita 
ExpPot        ::= ExpUnaria "**" ExpPot 
                | ExpUnaria

// Operadores prefixos
ExpUnaria     ::= ("-" | "!") ExpUnaria 
                | Fator

Fator         ::= "(" Expressao ")" 
                | ID 
                | ID "(" [ ListaArgs ] ")" 
                | INT_LIT 
                | DOUBLE_LIT 
                | STRING_LIT 
                | CHAR_LIT 
                | "true" 
                | "false"


```

### 1.2 Transformações aplicadas

Recursão à esquerda e fatoração à esquerda. Uma linha por correção.

| Não-terminal | Problema | Antes | Depois |
|---|---|---|---|
| **ExpOR** | Recursão à esquerda | `ExpOR ::= ExpOR "\|\|" ExpAND \| ExpAND` | `ExpOR ::= ExpAND ExpORResto`<br>`ExpORResto ::= "\|\|" ExpAND ExpORResto \| ε` |
| **ExpAND** | Recursão à esquerda | `ExpAND ::= ExpAND "&&" ExpIgualdade \| ExpIgualdade` | `ExpAND ::= ExpIgualdade ExpANDResto`<br>`ExpANDResto ::= "&&" ExpIgualdade ExpANDResto \| ε` |
| **ExpIgualdade** | Recursão à esquerda | `ExpIgualdade ::= ExpIgualdade ("==" \| "!=") ExpRelacional \| ExpRelacional` | `ExpIgualdade ::= ExpRelacional ExpIgualResto`<br>`ExpIgualResto ::= ("==" \| "!=") ExpRelacional ExpIgualResto \| ε` |
| **ExpRelacional** | Recursão à esquerda | `ExpRelacional ::= ExpRelacional ("<" \| ">" \| "<=" \| ">=") ExpAdd \| ExpAdd` | `ExpRelacional ::= ExpAdd ExpRelResto`<br>`ExpRelResto ::= ("<" \| ">" \| "<=" \| ">=") ExpAdd ExpRelResto \| ε` |
| **ExpAdd** | Recursão à esquerda | `ExpAdd ::= ExpAdd ("+" \| "-") ExpMult \| ExpMult` | `ExpAdd ::= ExpMult ExpAddResto`<br>`ExpAddResto ::= ("+" \| "-") ExpMult ExpAddResto \| ε` |
| **ExpMult** | Recursão à esquerda | `ExpMult ::= ExpMult ("*" \| "/" \| "%") ExpPot \| ExpPot` | `ExpMult ::= ExpPot ExpMultResto`<br>`ExpMultResto ::= ("*" \| "/" \| "%") ExpPot ExpMultResto \| ε` |
| **ExpPot** | Prefixo comum (ExpUnaria) | `ExpPot ::= ExpUnaria "**" ExpPot \| ExpUnaria` | `ExpPot ::= ExpUnaria ExpPotResto`<br>`ExpPotResto ::= "**" ExpPot \| ε` |
| **Fator** | Prefixo comum (`ID`) | `Fator ::= ID \| ID "(" [ ListaArgs ] ")" \| ...` | `Fator ::= ID FatorIdResto \| ...`<br>`FatorIdResto ::= "(" [ ListaArgs ] ")" \| ε` |
| **Atribuicao** / **ChamadaCmd** | Prefixo comum (`ID`) entre dois comandos | `Comando ::= Atribuicao \| ChamadaCmd \| ...`<br>`Atribuicao ::= ID "=" Expressao ";"`<br>`ChamadaCmd ::= ID "(" [ ListaArgs ] ")" ";"` | `Comando ::= CmdId \| ...`<br>`CmdId ::= ID CmdIdResto`<br>`CmdIdResto ::= "=" Expressao ";" \| "(" [ ListaArgs ] ")" ";"` |
| **DeclVar** / **DeclFuncao** | Prefixo comum (`Tipo ID`) | `DeclVar ::= Tipo ID [ "=" Expressao ] ";"`<br>`DeclFuncao ::= TipoRetorno ID "(" [ ListaParams ] ")" Bloco` | `Declaracao ::= DeclConst \| DeclVarFuncao \| DeclVoidFuncao`<br>`DeclVarFuncao ::= Tipo ID DeclResto`<br>`DeclResto ::= [ "=" Expressao ] ";" \| "(" [ ListaParams ] ")" Bloco`<br>`DeclVoidFuncao ::= "void" ID "(" [ ListaParams ] ")" Bloco` |
| **(vários)** | Conversão EBNF → BNF | `[ X ]` e `{ X }` | Cada um virou um não-terminal com ε: `ListaDeclaracoes`, `AtribuicaoOpcional`, `OpcionalParams`, `ListaParamsResto`, `ListaComandos`, `OpcionalArgs`, `ListaArgsResto`, `ListaElif`, `ElseOpcional`, `RetornoOpcional` |


### 1.3 Gramática final (BNF, LL(1))

```bnf
// --- ESQUELETO E DECLARAÇÕES ---
Programa            ::= ListaDeclaracoes
ListaDeclaracoes    ::= Declaracao ListaDeclaracoes | ε

Declaracao          ::= DeclConst | DeclVarFuncao | DeclVoidFuncao

DeclConst           ::= "const" Tipo ID "=" Expressao ";"
DeclVarFuncao       ::= Tipo ID DeclResto
DeclResto           ::= AtribuicaoOpcional ";" | "(" OpcionalParams ")" Bloco
DeclVoidFuncao      ::= "void" ID "(" OpcionalParams ")" Bloco

AtribuicaoOpcional  ::= "=" Expressao | ε
OpcionalParams      ::= ListaParams | ε

Tipo                ::= "int" | "double" | "bool" | "char" | "string"

ListaParams         ::= Tipo ID ListaParamsResto
ListaParamsResto    ::= "," Tipo ID ListaParamsResto | ε

// --- COMANDOS ---
Bloco               ::= "{" ListaComandos "}"
ListaComandos       ::= Comando ListaComandos | ε

Comando             ::= DeclConst 
                      | DeclVarLocal 
                      | CmdId 
                      | CmdIf 
                      | CmdWhile 
                      | CmdReturn 
                      | CmdPrint 
                      | CmdRead

DeclVarLocal        ::= Tipo ID AtribuicaoOpcional ";"

CmdId               ::= ID CmdIdResto
CmdIdResto          ::= "=" Expressao ";" | "(" OpcionalArgs ")" ";"

OpcionalArgs        ::= ListaArgs | ε
ListaArgs           ::= Expressao ListaArgsResto
ListaArgsResto      ::= "," Expressao ListaArgsResto | ε

CmdIf               ::= "if" "(" Expressao ")" Bloco ListaElif ElseOpcional
ListaElif           ::= "elif" "(" Expressao ")" Bloco ListaElif | ε
ElseOpcional        ::= "else" Bloco | ε

CmdWhile            ::= "while" "(" Expressao ")" Bloco
CmdReturn           ::= "return" RetornoOpcional ";"
RetornoOpcional     ::= Expressao | ε

CmdPrint            ::= "print" "(" Expressao ")" ";"
CmdRead             ::= "read" "(" ID ")" ";"

// --- EXPRESSÕES (LL(1) - Sem recursão à esquerda e fatoradas) ---
Expressao           ::= ExpOR

ExpOR               ::= ExpAND ExpORResto
ExpORResto          ::= "||" ExpAND ExpORResto | ε

ExpAND              ::= ExpIgualdade ExpANDResto
ExpANDResto         ::= "&&" ExpIgualdade ExpANDResto | ε

ExpIgualdade        ::= ExpRelacional ExpIgualResto
ExpIgualResto       ::= ("==" | "!=") ExpRelacional ExpIgualResto | ε

ExpRelacional       ::= ExpAdd ExpRelResto
ExpRelResto         ::= ("<" | ">" | "<=" | ">=") ExpAdd ExpRelResto | ε

ExpAdd              ::= ExpMult ExpAddResto
ExpAddResto         ::= ("+" | "-") ExpMult ExpAddResto | ε

ExpMult             ::= ExpPot ExpMultResto
ExpMultResto        ::= ("*" | "/" | "%") ExpPot ExpMultResto | ε

// Associatividade à direita via recursão encapsulada (fatorada)
ExpPot              ::= ExpUnaria ExpPotResto
ExpPotResto         ::= "**" ExpPot | ε

// Operadores prefixos unários
ExpUnaria           ::= ("-" | "!") ExpUnaria 
                      | Fator

Fator               ::= "(" Expressao ")" 
                      | ID FatorIdResto 
                      | INT_LIT 
                      | DOUBLE_LIT 
                      | STRING_LIT 
                      | CHAR_LIT 
                      | "true" 
                      | "false"

FatorIdResto        ::= "(" OpcionalArgs ")" | ε
```

### 1.4 Decisões de projeto da sintaxe

**Estrutura do programa**
- Um programa é uma sequência de declarações globais (variáveis, constantes e funções). Comandos só aparecem dentro de funções. A execução começa em main; a existência de main é verificada na análise semântica, não na gramática.
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

**Gramática aumentada usada no cálculo:** 
`S' ::= Programa $`

**Não-terminais anuláveis (derivam ε):** 
ListaDeclaracoes, AtribuicaoOpcional, OpcionalParams, ListaParamsResto, ListaComandos, OpcionalArgs, ListaArgsResto, ListaElif, ElseOpcional, RetornoOpcional, ExpORResto, ExpANDResto, ExpIgualResto, ExpRelResto, ExpAddResto, ExpMultResto, ExpPotResto, FatorIdResto, Programa.


| Não-terminal | FIRST | FOLLOW |
|---|---|---|
| **Programa** | `{"const", "int", "double", "bool", "char", "string", "void", ε}` | `{$}` |
| **ListaDeclaracoes** | `{"const", "int", "double", "bool", "char", "string", "void", ε}` | `{$}` |
| **Declaracao** | `{"const", "int", "double", "bool", "char", "string", "void"}` | `{"const", "int", "double", "bool", "char", "string", "void", $}` |
| **DeclConst** | `{"const"}` | `{"const", "int", "double", "bool", "char", "string", "void", ID, "if", "while", "return", "print", "read", "}", $}` |
| **DeclVarFuncao** | `{"int", "double", "bool", "char", "string"}` | `{"const", "int", "double", "bool", "char", "string", "void", $}` |
| **DeclResto** | `{"=", ";", "("}` | `{"const", "int", "double", "bool", "char", "string", "void", $}` |
| **DeclVoidFuncao** | `{"void"}` | `{"const", "int", "double", "bool", "char", "string", "void", $}` |
| **AtribuicaoOpcional** | `{"=", ε}` | `{";"}` |
| **OpcionalParams** | `{"int", "double", "bool", "char", "string", ε}` | `{")"}` |
| **Tipo** | `{"int", "double", "bool", "char", "string"}` | `{ID}` |
| **ListaParams** | `{"int", "double", "bool", "char", "string"}` | `{")"}` |
| **ListaParamsResto** | `{",", ε}` | `{")"}` |
| **Bloco** | `{"{"}` | `{"const", "int", "double", "bool", "char", "string", "void", "elif", "else", ID, "if", "while", "return", "print", "read", "}", $}` |
| **ListaComandos** | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", ε}` | `{"}"}` |
| **Comando** | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **DeclVarLocal** | `{"int", "double", "bool", "char", "string"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **CmdId** | `{ID}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **CmdIdResto** | `{"=", "("}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **OpcionalArgs** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false", ε}` | `{")"}` |
| **ListaArgs** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{")"}` |
| **ListaArgsResto** | `{",", ε}` | `{")"}` |
| **CmdIf** | `{"if"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **ListaElif** | `{"elif", ε}` | `{"else", "const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **ElseOpcional** | `{"else", ε}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **CmdWhile** | `{"while"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **CmdReturn** | `{"return"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **RetornoOpcional** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false", ε}` | `{";"}` |
| **CmdPrint** | `{"print"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **CmdRead** | `{"read"}` | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"}` |
| **Expressao** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{";", ",", ")"}` |
| **ExpOR** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{";", ",", ")"}` |
| **ExpORResto** | `{"||", ε}` | `{";", ",", ")"}` |
| **ExpAND** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"||", ";", ",", ")"}` |
| **ExpANDResto** | `{"&&", ε}` | `{"||", ";", ",", ")"}` |
| **ExpIgualdade** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"&&", "||", ";", ",", ")"}` |
| **ExpIgualResto** | `{"==", "!=", ε}` | `{"&&", "||", ";", ",", ")"}` |
| **ExpRelacional** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpRelResto** | `{"<", ">", "<=", ">=", ε}` | `{"==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpAdd** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpAddResto** | `{"+", "-", ε}` | `{"<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpMult** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpMultResto** | `{"*", "/", "%", ε}` | `{"+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpPot** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpPotResto** | `{"**", ε}` | `{"*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **ExpUnaria** | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"**", "*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **Fator** | `{"(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | `{"**", "*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |
| **FatorIdResto** | `{"(", ε}` | `{"**", "*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "||", ";", ",", ")"}` |

---

## 3. Verificação da condição LL(1) (Seção 2.3)

Para cada não-terminal com mais de uma alternativa:
(a) FIRST das alternativas disjuntos entre si;
(b) no máximo uma alternativa anulável;
(c) se há alternativa anulável, FIRST das demais disjunto de FOLLOW do não-terminal.

Não-terminais com uma única produção não têm decisão a verificar.

| Não-terminal | Alternativas → FIRST de cada uma | (a) FIRST disjuntos | (b) ≤ 1 anulável | (c) FIRST × FOLLOW | Resultado |
|---|---|---|---|---|---|
| **ListaDeclaracoes** | `Declaracao...` → `{"const", "int", "double", "bool", "char", "string", "void"}`<br>`ε` → `{ε}` | sim | sim | `{"const", "int", "double", "bool", "char", "string", "void"} ∩ {$} = ∅` | ok |
| **Declaracao** | `DeclConst` → `{"const"}`<br>`DeclVarFuncao` → `{"int", "double", "bool", "char", "string"}`<br>`DeclVoidFuncao` → `{"void"}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **DeclResto** | `AtribuicaoOpcional ";"` → `{"=", ";"}`<br>`"("...` → `{"("}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **AtribuicaoOpcional** | `"=" Expressao` → `{"="}`<br>`ε` → `{ε}` | sim | sim | `{"="} ∩ {";"} = ∅` | ok |
| **OpcionalParams** | `ListaParams` → `{"int", "double", "bool", "char", "string"}`<br>`ε` → `{ε}` | sim | sim | `{"int", "double", "bool", "char", "string"} ∩ {")"} = ∅` | ok |
| **Tipo** | `"int"` → `{"int"}`<br>`"double"` → `{"double"}`<br>`"bool"` → `{"bool"}`<br>`"char"` → `{"char"}`<br>`"string"` → `{"string"}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **ListaParamsResto** | `"," Tipo...` → `{","}`<br>`ε` → `{ε}` | sim | sim | `{","} ∩ {")"} = ∅` | ok |
| **ListaComandos** | `Comando...` → `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read"}`<br>`ε` → `{ε}` | sim | sim | `{"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read"} ∩ {"}"} = ∅` | ok |
| **Comando** | `DeclConst` → `{"const"}`<br>`DeclVarLocal` → `{"int", "double", "bool", "char", "string"}`<br>`CmdId` → `{ID}`<br>`CmdIf` → `{"if"}`<br>`CmdWhile` → `{"while"}`<br>`CmdReturn` → `{"return"}`<br>`CmdPrint` → `{"print"}`<br>`CmdRead` → `{"read"}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **CmdIdResto** | `"=" Expressao ";"` → `{"="}`<br>`"(" OpcionalArgs...` → `{"("}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **OpcionalArgs** | `ListaArgs` → `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}`<br>`ε` → `{ε}` | sim | sim | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"} ∩ {")"} = ∅` | ok |
| **ListaArgsResto** | `"," Expressao...` → `{","}`<br>`ε` → `{ε}` | sim | sim | `{","} ∩ {")"} = ∅` | ok |
| **ListaElif** | `"elif" "("...` → `{"elif"}`<br>`ε` → `{ε}` | sim | sim | `{"elif"} ∩ {"else", "const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"} = ∅` | ok |
| **ElseOpcional** | `"else" Bloco` → `{"else"}`<br>`ε` → `{ε}` | sim | sim | `{"else"} ∩ {"const", "int", "double", "bool", "char", "string", ID, "if", "while", "return", "print", "read", "}"} = ∅` | ok |
| **RetornoOpcional** | `Expressao` → `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}`<br>`ε` → `{ε}` | sim | sim | `{"-", "!", "(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"} ∩ {";"} = ∅` | ok |
| **ExpORResto** | `"\|\|" ExpAND...` → `{"\|\|"}`<br>`ε` → `{ε}` | sim | sim | `{"\|\|"} ∩ {";", ",", ")"} = ∅` | ok |
| **ExpANDResto** | `"&&" ExpIgualdade...` → `{"&&"}`<br>`ε` → `{ε}` | sim | sim | `{"&&"} ∩ {"\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpIgualResto** | `("==" \| "!=")...` → `{"==", "!="}`<br>`ε` → `{ε}` | sim | sim | `{"==", "!="} ∩ {"&&", "\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpRelResto** | `("<" \| ">" \| "<=" \| ">=")...` → `{"<", ">", "<=", ">="}`<br>`ε` → `{ε}` | sim | sim | `{"<", ">", "<=", ">="} ∩ {"==", "!=", "&&", "\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpAddResto** | `("+" \| "-")...` → `{"+", "-"}`<br>`ε` → `{ε}` | sim | sim | `{"+", "-"} ∩ {"<", ">", "<=", ">=", "==", "!=", "&&", "\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpMultResto** | `("*" \| "/" \| "%")...` → `{"*", "/", "%"}`<br>`ε` → `{ε}` | sim | sim | `{"*", "/", "%"} ∩ {"+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpPotResto** | `"**" ExpPot` → `{"**"}`<br>`ε` → `{ε}` | sim | sim | `{"**"} ∩ {"*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "\|\|", ";", ",", ")"} = ∅` | ok |
| **ExpUnaria** | `("-" \| "!") ExpUnaria` → `{"-", "!"}`<br>`Fator` → `{"(", ID, INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **Fator** | `"(" Expressao ")"` → `{"("}`<br>`ID FatorIdResto` → `{ID}`<br>Literais → `{INT_LIT, DOUBLE_LIT, STRING_LIT, CHAR_LIT, "true", "false"}` | sim | sim (nenhuma anulável) | não se aplica | ok |
| **FatorIdResto** | `"(" OpcionalArgs ")"` → `{"("}`<br>`ε` → `{ε}` | sim | sim | `{"("} ∩ {"**", "*", "/", "%", "+", "-", "<", ">", "<=", ">=", "==", "!=", "&&", "\|\|", ";", ",", ")"} = ∅` | ok |

### 3.1 Violações encontradas e correções

Nenhuma violação foi encontrada; a gramática é LL(1). O conflito clássico do *dangling else* não ocorre porque os blocos de `if`/`elif`/`else` exigem chaves obrigatoriamente (decisão da Secção 1.4): o `}` fecha o bloco sintático de forma inequívoca, e `else` não pertence ao FOLLOW de nenhuma alternativa anulável que concorra com ele.

---

## 4. Da gramática ao código

Ajuda a apontar, para cada decisão do parser, de onde ela vem (objetivo da Seção 1 do enunciado).

| Não-terminal | Método | Decide a alternativa por | Aceita ε quando | Sincroniza em |
|---|---|---|---|---|
| <!-- TODO --> | `parseX()` | FIRST(...) | lookahead ∈ FOLLOW(...) | FOLLOW(...) + âncoras |
