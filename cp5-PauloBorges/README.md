# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 5 — Verificação de tipos**.

## Estrutura

| Item do checklist (CP5) | Caminho neste checkpoint |
|---|---|
| Regras de tipagem, tabela de operadores e as 11 decisões (à mão) | `tipos-a-mao.pdf` |
| Tipo semântico (`Type`) e a tabela de operadores em Java (`Types`) | `src/br/ufu/compiladores/types/` |
| Verificador de tipos (`TypeChecker`) | `src/br/ufu/compiladores/visitor/` |
| Campo de tipo nas expressões | `src/br/ufu/compiladores/ast/Expr.java` |
| Testes das regras isoladas (tabela de operadores, sem parser) | `test/br/ufu/compiladores/types/TypesTest.java` |
| Testes do verificador (programas obrigatórios, funções, avisos, uma decisão por programa) | `test/br/ufu/compiladores/types/TypeCheckerTest.java` |
| Programas de teste do CP5 e saídas esperadas, escritas à mão | `test/programas/*.txt`, `test/esperado/*.txt` |
| Regressão do CP4 (escopo), do CP3 (AST) e do CP2 (parser) | `IdentificationTest`, `SymbolTableTest`, `AstTest`, `ParserTest` |
| Log de uso de IA | `log-uso-ia.md` |

O projeto de escopo continua em `escopo-design.md`, o da AST em `ast-design.md` e a gramática em `gramatica-first-follow.md`.

> **Nota sobre a estrutura:** o projeto é Maven, com o código em `src/` e os testes em `test/`. O `pom.xml` redefine as pastas padrão (`sourceDirectory`/`testSourceDirectory`).

## Como os tipos estão representados

O tipo semântico é a classe `types.Type`, separada do `ast.Type`. O `ast.Type` é um nó da árvore: guarda o tipo como foi escrito no fonte, com linha e coluna. O `types.Type` é o valor que as passadas comparam. A conversão de um para o outro é `Types.of(nome)`.

- Os tipos primitivos são instâncias fixas: `Type.INT`, `DOUBLE`, `BOOL`, `CHAR` e `STRING`. Como existe uma instância só de cada, a comparação é feita com `==`.
- `Type.VOID` é o retorno dos procedimentos e não aparece como tipo de variável.
- `Type.ERROR` é o tipo de uma expressão que já deu erro. Ele sobe pela árvore sem gerar nova mensagem e é aceito em qualquer posição, o que evita erros em cascata.
- `Type.FuncType` é a assinatura de funções e procedimentos: a lista de tipos dos parâmetros e o tipo de retorno (`VOID` no procedimento). Ela é impressa como `(int, int) -> int`.

O tipo fica guardado em dois lugares:

- **`Symbol.type`:** preenchido pela passada de identificação no momento da declaração. As variáveis, constantes e parâmetros recebem o tipo declarado; as funções e procedimentos recebem a assinatura.
- **`Expr.type`:** preenchido pelo verificador em cada expressão. É a única mudança na hierarquia da AST.

## Onde ficam as regras

| Regras da Parte 1 | Onde estão |
|---|---|
| Tabela de operadores (ARIT-INT, ARIT-DOUBLE, CONCAT, REL-NUM, REL-CHAR, IGUAL, IGUAL-NUM, LOGICO) | `Types.binary` |
| Unários (NEG, NAO) | `Types.unary` |
| Compatibilidade (int → double, ERROR aceito em tudo) | `Types.assignable` |
| ERRO-PROPAGA | `Types.binary`/`Types.unary` devolvem `ERROR`; `TypeChecker.visitBin`/`visitUn` só reclamam se nenhum operando era `ERROR` |
| Literais, ID, ID-ERRO, ID-FUNC | `TypeChecker.visit*Literal`, `TypeChecker.visitId` |
| DECL, DECL-INIT | `TypeChecker.visitVarDecl` |
| ATRIB | `TypeChecker.visitAssign` (alvo conferido em `alvo`) |
| BLOCO, IF-ELIF, WHILE | `TypeChecker.visitBlock`, `visitIf`, `visitElif`, `visitWhile` (condição em `condicao`) |
| RETURN-VAL, RETURN-VOID | `TypeChecker.visitReturn` |
| FUNC e retornaSempre | `TypeChecker.visitFuncDecl` + `visitor/RetornaSempre.java` |
| CALL-EXPR, CALL-CMD | `TypeChecker.chamada`, usada por `visitCallExpr` e `visitCallCmd` |
| PRINT, READ | `TypeChecker.visitPrint`, `visitRead` |
| Variáveis sem valor (aviso) | `TypeChecker`: conjunto `semValor`, atualizado em `visitVarDecl`, `visitAssign`, `visitRead`, `visitIf`, `visitWhile` e conferido em `visitId` |

| Decisão | Onde está |
|---|---|
| 1. int → double | `Types.assignable` e `Types.binary` |
| 2. char com int | `Types.binary` |
| 3. bool em aritmética | `Types.binary`; condição só bool em `TypeChecker.condicao` |
| 4. string | `Types.binary` |
| 5. `==` entre tipos diferentes | `Types.comparableByEquality` |
| 6. Função como comando | `TypeChecker.visitCallCmd` (o retorno é descartado) |
| 7. Recursão mútua | `IdentificationVisitor` (CP4): declarar antes de usar |
| 8. Argumentos | `TypeChecker.chamada`, com `Types.assignable(parâmetro, argumento)` |
| 9. return em todos os caminhos | `RetornaSempre` + `TypeChecker.visitFuncDecl` |
| 10. Sobrecarga | `IdentificationVisitor.declarar` (erro de redeclaração) |
| 11. Função sem parênteses | `TypeChecker.visitId` |

O verificador é um `Visitor<Type>` e o `RetornaSempre` é um `Visitor<Boolean>`. Nenhum dos dois usa `instanceof` nem `switch` sobre o tipo do nó, e o verificador não declara nomes nem abre escopos: ele lê o `decl` deixado pela identificação.

A MiniLang não tem funções aninhadas (só globais) nem `break`. Por isso esses dois casos não têm programa de teste.

## Formato das mensagens

Cada mensagem sai como `linha N: texto`. A saída junta os erros da identificação, os erros de tipo e os avisos, ordenados pela linha; numa mesma linha, os erros da identificação vêm primeiro.

```
tipos incompatíveis em 'op': T1 e T2
operando incompatível em 'op': T
tipos incompatíveis na inicialização de 'x': T1 recebe T2
tipos incompatíveis na atribuição a 'x': T1 recebe T2
'x' é constante: não pode receber valor          (também "é função" / "é procedimento")
'read' não aceita 'x', que é T
condição de 'if' deve ser bool, mas é T          (também 'elif' e 'while')
'f' devolve T1, mas 'return' dá T2
'return' sem valor em 'f', que devolve T
procedimento 'p' não devolve valor
função 'f' pode terminar sem 'return'            (na linha da declaração da função)
'f' espera N argumento(s), recebeu M
argumento K de 'f': esperado T1, veio T2
'p' é procedimento e não devolve valor
'x' não é função nem procedimento
'f' é função: não pode ser usado sem parênteses  (também "é procedimento")
aviso: 'x' pode estar sem valor
```

Do CP4 continuam `'x' não declarado` e `'x' já declarado neste escopo (linha Y)`.

## Como compilar e executar

```bash
mvn clean compile  # compila scanner, parser, AST, visitors, tabela de símbolos e verificador
mvn clean test     # roda as suítes do scanner, do parser (CP2), da AST (CP3), de escopo (CP4) e de tipos (CP5)

# passada de identificação: resoluções dos usos (x@7 -> x@6) e erros de escopo
java -cp target/classes br.ufu.compiladores.Main --escopo test/programas/escopo/sombreamento.ml

# imprime a AST do programa (impressor de árvore)
java -cp target/classes br.ufu.compiladores.Main --arvore test/programas/validos/valido01.ml

# reimprime o código-fonte a partir da AST, na forma canônica (reimpressor)
java -cp target/classes br.ufu.compiladores.Main --reimprimir test/programas/validos/valido01.ml

# análise sintática, como no CP2: ACEITO ou REJEITADO + erros com linha/coluna
java -cp target/classes br.ufu.compiladores.Main test/programas/validos/valido01.ml

# lista de tokens, como no CP1
java -cp target/classes br.ufu.compiladores.Main --tokens test/programas/validos/valido01.ml

# verificação de tipos: erros, avisos e veredito
java -cp target/classes br.ufu.compiladores.Main --tipos test/programas/varios-erros.txt
```

Em `--escopo`, a saída segue o formato da seção 5 do `escopo-design.md`: primeiro as resoluções, depois os erros. Um programa com erro léxico ou sintático não chega à passada: os erros são relatados como no CP2.

Em `--tipos`, a saída traz os erros e os avisos, ordenados por linha, e no final o veredito: `TIPOS OK` ou `REJEITADO (N erro(s))`. Avisos não contam como erro. Com erro, o compilador para aí: a geração de código (CP6) só vai rodar quando N for 0.