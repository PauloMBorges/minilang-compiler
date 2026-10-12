# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 1 — Análise Léxica (scanner à mão)**.

## Estrutura

| Item do checklist | Caminho neste repositório |
|---|---|
| Especificação léxica (Markdown) | `especificacao-lexica.md` |
| Código-fonte do scanner (Java) | `src/main/java/br/ufu/compiladores/` |
| Suíte de testes | `src/test/java/br/ufu/compiladores/lexer/ScannerTest.java` |
| Desenhos do AFD (id/reservada e string) | `automatos/` |
| Evidência de execução (saída/log) | `evidencias/` |
| Log de uso de IA | `log-uso-ia.md` |

> **Nota sobre a estrutura:** o projeto usa layout Maven (`src/main/java`,
> `src/test/java`), que contém o `src/` e o `test/` pedidos no enunciado,
> apenas mais aninhados. A tabela acima mapeia cada item para seu caminho.

## Como compilar e executar

```bash
mvn compile        # compila o scanner
mvn test           # roda a suíte de testes (JUnit)
java -cp target/classes br.ufu.compiladores.Main examples/exemplo01.ml   # tokeniza um exemplo
java -cp target/classes br.ufu.compiladores.Main examples/comerro.ml     # exemplo com erros (recuperação)
```

