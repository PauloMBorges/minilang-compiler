# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC

Compilador incremental da disciplina. Cada checkpoint fica numa pasta própria, completa e independente, exatamente no formato em que foi entregue.

| Checkpoint | Pasta | Conteúdo |
|---|---|---|
| CP1 — Análise léxica | [`cp1-PauloBorges/`](cp1-PauloBorges/) | Especificação léxica, scanner manual, AFDs, testes |
| CP2 — Gramática e parser recursivo descendente | [`cp2-PauloBorges/`](cp2-PauloBorges/) | Gramática LL(1) com FIRST/FOLLOW, parser reconhecedor com modo pânico, testes |

Cada pasta é um projeto Maven separado. Para compilar e testar um checkpoint, entre na pasta dele:

```bash
cd cp2-PauloBorges
mvn test
```

As instruções específicas estão no `README.md` de cada pasta.
