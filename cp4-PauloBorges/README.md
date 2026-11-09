# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 4 — Tabela de símbolos e passada de identificação**.

## Estrutura

| Item do checklist (CP4) | Caminho neste checkpoint |
|---|---|
| Ambiente, `Symbol`, escolha da implementação e as cinco decisões de escopo | `escopo-design.md` |
| `SymbolTable`, a implementação escolhida (`StackedSymbolTable`), `Symbol`, `SymbolCategory` e `SemanticError` | `src/br/ufu/compiladores/symbols/` |
| Passada de identificação (`IdentificationVisitor`) e impressor das resoluções (`ResolutionPrinter`) | `src/br/ufu/compiladores/visitor/` |
| Campo `decl` nos nós de uso (`IdExpr`, `AssignCmd`, `ReadCmd`, `CallCmd`, `CallExpr`) | `src/br/ufu/compiladores/ast/` |
| Testes da tabela isolada | `test/br/ufu/compiladores/symbols/SymbolTableTest.java` |
| Testes da passada (programas obrigatórios, decisões, erros, recursão, uso após o bloco) | `test/br/ufu/compiladores/symbols/IdentificationTest.java` |
| Programas de teste do CP4 | `test/programas/escopo/` |
| Saídas esperadas, escritas à mão | `test/esperado/` |
| Regressão do CP3 (AST e Visitor) e do CP2 (parser) | `test/br/ufu/compiladores/ast/AstTest.java`, `test/br/ufu/compiladores/parser/ParserTest.java` |
| Saídas de execução (passada nos programas de escopo, `mvn test`) | `evidencias/` |
| Log de uso de IA | `log-uso-ia.md` |

O projeto da AST continua em `ast-design.md` e a gramática em `gramatica-first-follow.md` (os mesmos do CP3). A especificação léxica e os autômatos continuam em `cp1-PauloBorges/` e `cp2-PauloBorges/`.

> **Nota sobre a estrutura:** o projeto é Maven, com o código em `src/` e os testes em `test/`. O `pom.xml` redefine as pastas padrão (`sourceDirectory`/`testSourceDirectory`).

## Como compilar e executar

```bash
mvn clean compile  # compila scanner, parser, AST, visitors e tabela de símbolos
mvn clean test     # roda as suítes do scanner, do parser (CP2), da AST (CP3) e de escopo (CP4)

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