# Log de uso de IA

### 2026-09-20 — Estrutura inicial do projeto
- **Ferramenta:** Claude
- **Contexto/tarefa:** criar estrutura de pastas Maven, .gitignore, esqueletos de arquivos e este log.
- **Prompt (resumo):** montar a base do repositório para o scanner e a estratégia de log de IA.
- **O que aceitei:** estrutura de diretórios, pom.xml, .gitignore, enum TokenType e tabela de reservadas (refletem a especificação que eu defini), record Token, LexicalError, mecânica de buffer (peek/advance) do Scanner.
- **O que rejeitei/alterei:** a lógica de reconhecimento dos AFDs (scanIdentifierOrKeyword, scanNumber, scanString, scanOperatorOrPunctuation) não foi gerada por IA - ficou como esqueleto para eu implementar.

### 2026-09-20 — Revisão do scanner e geração dos casos de teste
- **Ferramenta:** Claude
- **Contexto/tarefa:** eu já tinha implementado os quatro AFDs do Scanner à mão (identificador, número, string, operadores). Pedi (1) uma revisão do meu código e (2) ajuda para montar os casos de teste JUnit, incluindo os de erro.
- **Prompt (resumo):** "Avalie os scanners que escrevi. Depois, me mostre como testar."
- **O que aceitei:**
  - Os apontamentos da revisão sobre o meu código: o fall-through no `case '&'` (faltava `break`) e o furo na validação de `_` em números (caso `1_e5`). Corrigi ambos eu mesmo - o `break` e um helper `consumeDigits` que valida o `_` centralizadamente.
  - A classe de testes `ScannerTest` (JUnit) sugerida, que cobre cada categoria de token e os casos de erro (string sem fechar, expoente sem dígito, `&` isolado, caractere inesperado).
- **O que rejeitei/alterei:** a lógica de reconhecimento dos AFDs não veio da IA - foi escrita por mim antes desta conversa; a IA apenas revisou. Ajustei o helper e as mensagens de erro por conta própria.
- **Verificação:** rodei `mvn test` → 19 testes, 0 falhas. Rodei também `Main` sobre `examples/exemplo01.ml` e conferi a lista de tokens à mão contra a especificação.

### 2026-09-24 - Alteração no código para recuperação de erro
- **Ferramenta:** Claude
- **Contexto/tarefa:** precisei alterar o código para recuperação de erros lexicos (lista + try/catch) e os testes
- **Prompt:** 
"Atualmente, meu método nextToken() (código abaixo) aciona um autômato e lança uma exceção (throw new LexicalError) no primeiro caractere inválido, interrompendo o programa. Preciso alterar isso para uma estratégia de recuperação de erros: o scanner deve registrar o erro (em uma lista), avançar o cursor e continuar tokenizando o restante do arquivo. Você pode me explicar como estruturar a sintaxe do try-catch dentro desse laço while para capturar a exceção e, principalmente, qual a lógica para garantir que o cursor avance em caso de falha para eu não cair em um loop infinito? Por favor, foque apenas em explicar a estrutura desse trecho e tirar essa dúvida de sintaxe, sem gerar a especificação do scanner inteiro."
- **O que aceitei:**
  - A estrutura de bloco try-catch para isolar a captura da exceção LexicalError, a estratégia de criar uma variável (posAntes) antes da tentativa de leitura e a lógica condicional (if (pos == posAntes && hasNext()) { advance(); }) para forçar o avanço do cursor apenas quando o autômato falha sem consumir nenhum caractere, evitando laços infinitos.
- **O que rejeitei/alterei:** Sugestões para mudar a estrutura da classe inteira e mexer na lógica interna dos AFDs (scanNumber, scanXxx...) que eu já havia escrito 
