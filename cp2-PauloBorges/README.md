# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 2 — Gramática e parser recursivo descendente (reconhecedor)**.

## Estrutura

| Item do checklist (CP2) | Caminho neste repositório |
|---|---|
| Gramática final, FIRST/FOLLOW e verificação LL(1) | `gramatica-first-follow.md` |
| Código-fonte do parser (Java) | `src/br/ufu/compiladores/parser/` |
| Scanner do CP1 (sem modificação) | `src/br/ufu/compiladores/lexer/` |
| Suíte de testes do parser | `test/br/ufu/compiladores/parser/ParserTest.java` |
| Programas de teste (válidos, inválidos, múltiplos erros) | `test/programas/` |
| Log de uso de IA | `log-uso-ia.md` |

Material do CP1, mantido no repositório: `especificacao-lexica.md`, `automatos/`, `evidencias/`, `examples/` e `test/br/ufu/compiladores/lexer/ScannerTest.java`.

> **Nota sobre a estrutura:** o projeto continua sendo Maven, mas o código fica em `src/` e os testes em `test/`, como pede o enunciado. O `pom.xml` redefine as pastas padrão (`sourceDirectory`/`testSourceDirectory`), então os comandos abaixo funcionam normalmente.

## Como compilar e executar

```bash
mvn compile        # compila scanner + parser
mvn test           # roda as suítes do scanner e do parser (JUnit)

# análise sintática de um arquivo: imprime ACEITO ou REJEITADO + erros com linha/coluna
java -cp target/classes br.ufu.compiladores.Main test/programas/validos/valido01.ml

# lista de tokens, como no CP1
java -cp target/classes br.ufu.compiladores.Main --tokens examples/exemplo01.ml
java -cp target/classes br.ufu.compiladores.Main --tokens examples/exemploComErro.ml
```
