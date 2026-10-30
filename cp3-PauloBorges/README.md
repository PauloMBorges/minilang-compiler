# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 3 — Árvore de sintaxe abstrata (AST) e Visitor**.

## Estrutura

| Item do checklist (CP3) | Caminho neste checkpoint |
|---|---|
| Projeto da AST (hierarquia, mapeamento, decisões, posição, formato do impressor, árvore à mão) | `ast-design.md` |
| Hierarquia de nós e `accept` | `src/br/ufu/compiladores/ast/` |
| Interface `Visitor<T>` e os dois visitors (impressor e reimpressor) | `src/br/ufu/compiladores/visitor/` |
| Parser que devolve a AST | `src/br/ufu/compiladores/parser/` |
| Scanner do CP1 (sem modificação) | `src/br/ufu/compiladores/lexer/` |
| Testes da AST (árvores esperadas, precedência, parênteses, ida e volta, regressão) | `test/br/ufu/compiladores/ast/AstTest.java` |
| Árvores esperadas, escritas à mão | `test/esperado/` |
| Regressão do CP2 (veredictos e linhas dos erros) | `test/br/ufu/compiladores/parser/ParserTest.java` |
| Log de uso de IA | `log-uso-ia.md` |

A gramática do parser está em `gramatica-first-follow.md` (a mesma do CP2). A especificação léxica e os autômatos continuam em `cp1-PauloBorges/` e `cp2-PauloBorges/`.

> **Nota sobre a estrutura:** o projeto é Maven, com o código em `src/` e os testes em `test/`. O `pom.xml` redefine as pastas padrão (`sourceDirectory`/`testSourceDirectory`).

## Como compilar e executar

```bash
mvn compile        # compila scanner, parser, AST e visitors
mvn test           # roda as suítes do scanner, do parser (CP2) e da AST (CP3)

# análise sintática de um arquivo: ACEITO ou REJEITADO + erros com linha/coluna
java -cp target/classes br.ufu.compiladores.Main test/programas/validos/valido01.ml

# lista de tokens, como no CP1
java -cp target/classes br.ufu.compiladores.Main --tokens test/programas/validos/valido01.ml
```

<!-- TODO: acrescentar os modos de imprimir a árvore e de reimprimir o código-fonte quando existirem. -->
