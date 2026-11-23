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

<!-- TODO: a classe types.Type (e por que não é o ast.Type), os tipos primitivos, VOID, o tipo de erro e a assinatura de função; onde o tipo fica (Symbol.type e o campo de tipo de cada expressão). -->

## Onde ficam as regras

<!-- TODO: para cada grupo de regras e cada decisão da Parte 1, o arquivo e o método que a implementa (ex.: [ARIT-INT] → Types.binary; [ATRIB] → TypeChecker.visitAssign). -->

## Formato das mensagens

<!-- TODO: o texto de cada tipo de erro e do aviso, com linha, operador ou nome e os tipos. -->

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
```

Em `--escopo`, a saída segue o formato da seção 5 do `escopo-design.md`: primeiro as resoluções, depois os erros. Um programa com erro léxico ou sintático não chega à passada: os erros são relatados como no CP2.

<!-- TODO: modo do Main que roda o verificador (com erro, o compilador não segue para a geração de código). -->
