# MiniLang Compiler

**Aluno:** Paulo Borges · Construção de Compiladores · UFU.FACOM.BCC
Compilador incremental da disciplina de Construção de Compiladores (UFU).
Etapa atual: **Checkpoint 1 — Análise Léxica (scanner à mão)**.

## Estrutura

```
minilang/
├── pom.xml                     # build Maven
├── README.md
├── .gitignore                 
├── docs/
│   ├── AI_LOG.md              
│   └── ESPECIFICACAO_TOKENS.md 
├── examples/
│   └── exemplo01.ml            # fonte de exemplo para testar o scanner
├── src/main/java/br/ufu/compiladores/
│   ├── Main.java               # lê um arquivo e imprime os tokens
│   └── lexer/
│       ├── TokenType.java      # enum das categorias (da especificação)
│       ├── ReservedWords.java  # tabela hash de reservadas
│       ├── Token.java          # (type, lexeme, line, col)
│       ├── LexicalError.java   # erro léxico com posição
│       └── Scanner.java        # AFDs
└── src/test/java/.../lexer/
    └── ScannerTest.java        # esqueleto de testes (inclui casos de erro)
```

## Comandos (os três que importam)

```bash
mvn compile        # compila
mvn test           # roda os testes JUnit
mvn exec:java -Dexec.mainClass=br.ufu.compiladores.Main -Dexec.args=examples/exemplo01.ml
# (ou, mais simples, depois de compilar:)
java -cp target/classes br.ufu.compiladores.Main examples/exemplo01.ml
```

