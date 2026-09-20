# Log de uso de IA

### 2026-09-20 — Estrutura inicial do projeto
- **Ferramenta:** Claude
- **Contexto/tarefa:** criar estrutura de pastas Maven, .gitignore, esqueletos de arquivos e este log.
- **Prompt (resumo):** montar a base do repositório para o scanner e a estratégia de log de IA.
- **O que aceitei:** estrutura de diretórios, pom.xml, .gitignore, enum TokenType e tabela de reservadas (refletem a especificação que EU defini), record Token, LexicalError, mecânica de buffer (peek/advance) do Scanner.
- **O que rejeitei/alterei:** a lógica de reconhecimento dos AFDs (scanIdentifierOrKeyword, scanNumber, scanString, scanOperatorOrPunctuation) NÃO foi gerada por IA - ficou como esqueleto para eu implementar.

