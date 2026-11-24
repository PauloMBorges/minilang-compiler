# Log de uso de IA

## Checkpoint 1 — Análise léxica

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

## Checkpoint 2 — Gramática e parser recursivo descendente

### 2026-10-12 — Estrutura de entrega do CP2 (boilerplate)
- **Ferramenta:** Claude
- **Contexto/tarefa:** adaptar o projeto Maven do CP1 ao formato de entrega do CP2 (`src/`, `test/`, documentos na raiz) e criar os esqueletos.
- **Prompt (resumo):** "Me indique o que preciso modificar e criar para seguir a estrutura e deixar tudo pronto para o CP2. Pode criar todo boilerplate necessário, seguindo os slides."
- **O que foi gerado:**
  - reorganização do repositório em uma pasta por checkpoint (`cp1-PauloBorges/` intocada, `cp2-PauloBorges/` ao lado) e `README.md` da raiz como índice;
  - mudança de layout do CP2: código em `src/`, testes em `test/` e `pom.xml` apontando para essas pastas (`sourceDirectory`/`testSourceDirectory`); os arquivos do pacote `lexer` foram só movidos, sem alteração de conteúdo;
  - `Parser.java` apenas com a infraestrutura mostrada na Aula 6 (campos, construtor, `parse()`, `match()`, `error()`, `sincronizar()`) e um molde comentado; **nenhum método `parseX()` da MiniLang foi gerado**;
  - `SyntaxError.java` (registro de erro com linha/coluna);
  - `Main.java`: modo de análise sintática (ACEITO/REJEITADO) e opção `--tokens` para manter o comportamento do CP1;
  - `ParserTest.java`: helpers e os 7 testes exigidos, desativados (`@Disabled`) até os programas e o parser existirem; os arquivos de `test/programas/` têm só um comentário descrevendo o que cada um deve cobrir;
  - templates vazios de `gramatica-first-follow.md` (seções 2.1–2.3 do enunciado) e atualização do `README.md`.

### 2026-10-12 — Revisão e correção dos conjuntos FIRST e FOLLOW
- **Ferramenta:** Claude
- **Contexto/tarefa:** revisão e validação dos conjuntos FIRST e FOLLOW (Seção 2.2) calculados previamente para a gramática LL(1) da linguagem.
- **Prompt:** "Aqui estão os conjuntos FIRST e FOLLOW que calculei para a minha gramática. Pode revisar, apontar se esqueci de algo ou corrigir possíveis erros?"
- **O que foi gerado:**
  - validação da tabela original submetida;
  - correção de itens calculados incorretamente, ajustando a propagação de conjuntos em regras com derivação vazia (ε);
  - identificação e inclusão de terminais ausentes que haviam sido esquecidos durante o cálculo manual;
  - tabela final revisada, corrigida e formatada em Markdown, pronta para ser incorporada ao arquivo gramatica-first-follow.md.

### 2026-10-13 — Redação e estruturação da Seção 4 (Mapeamento Gramática-Código)
- **Ferramenta:** Claude
- **Contexto/tarefa:** Criação da Seção 4 do documento gramatica-first-follow.md, mapeando a implementação em Java (Parser.java) para a gramática formal LL(1).
- **Prompt:** "Escreva a Seção 4 explicando como cada não-terminal virou um método parseX(), as decisões baseadas em FIRST/FOLLOW, a sincronização e as diferenças entre a gramática teórica e o código final implementado."
- **O que foi gerado:**
  - Tabela detalhada associando não-terminais aos métodos, condições de decisão por FIRST, validação de ε via FOLLOW e conjuntos de sincronização do modo pânico.
  - Subseção 4.1 documentando as divergências estruturais intencionais (como a conversão de recursão à direita em laços while, a manutenção da recursividade para a potência ** e o ajuste de âncoras adicionais).

## Checkpoint 3 — AST e Visitor

### 2026-10-29 — Estrutura de entrega do CP3 (boilerplate)
- **Ferramenta:** Claude
- **Contexto/tarefa:** criar a pasta do CP3 a partir do CP2 e os esqueletos da entrega, sem projetar a hierarquia de nós nem implementar visitors ou parser.
- **Prompt (resumo):** pedi a mesma divisão do CP2: estrutura e boilerplate pela IA, projeto e implementação por mim
- **O que foi gerado:**
  - `cp3-PauloBorges/` como cópia do CP2 (scanner sem modificação, parser e testes do CP2), `pom.xml` com artifactId/versão do CP3;
  - pacotes `ast/` e `visitor/`, com `Node` (posição linha/coluna e `accept` abstrato) e a interface `Visitor<T>` vazia; **nenhum nó concreto, visitor ou alteração no parser foi gerado**;
  - `AstTest.java` com os testes da Seção 5 desativados (`@Disabled`) e helpers marcados com TODO;
  - `test/esperado/README.md` com a convenção de nomes (os arquivos esperados serão escritos à mão);
  - template de `ast-design.md` com as seções pedidas no enunciado, e atualização dos `README.md`.

### 2026-10-29 — Revisões e fechamento do CP3
- **Ferramenta:** Claude
- **Contexto/tarefa:** revisão do parser que devolve a AST, do AstTest e do AstUnparser (escritos por mim) e fechamento da entrega.
- **Prompt (resumo):** envio do código para revisão; 
- **O que foi gerado:**
  - revisão do Parser: apontou o CharLiteral (lexema sem aspas, usar charAt(0)) e o VOID aceito em parseTipo; as correções foram feitas por mim;
  - AstTest: @Disabled temporário nos testes de ida e volta e mensagens de falha com a lista de erros; a lógica dos helpers é minha;
  - AstUnparser: parênteses no operando de UnExpr, depois de a revisão mostrar que (-a) ** b voltaria como -a ** b; o resto do reimpressor é meu;
  - Main com os modos --arvore e --reimprimir; README do CP3 e da raiz.

## Checkpoint 4 — Tabela de símbolos e passada de identificação

### 2026-11-08 — Estrutura de entrega do CP4 (boilerplate)
- **Ferramenta:** Claude
- **Contexto/tarefa:** criar a pasta do CP4 a partir do CP3 e os esqueletos da entrega, sem desenhar o ambiente, sem decidir as regras de escopo e sem implementar a tabela ou a passada.
- **Prompt (resumo):** enunciado do CP4; mesma divisão dos checkpoints anteriores: estrutura e boilerplate pela IA, projeto e implementação por mim.
- **O que foi gerado:**
  - `cp4-PauloBorges/` como cópia do CP3 (scanner, parser, AST, visitors e testes sem modificação), `pom.xml` com artifactId/versão do CP4;
  - pacote `symbols/`: a interface `SymbolTable` com as assinaturas do enunciado, `Symbol` vazio (campos a definir no `escopo-design.md`) e o registro `SemanticError` (linha + mensagem); **nenhuma implementação da tabela foi gerada**;
  - `IdentificationVisitor` só com o esqueleto (métodos do `Visitor` vazios, lista de erros e `getErrors()`); **nenhuma regra de escopo ou de declaração foi gerada**;
  - `SymbolTableTest` e `IdentificationTest` com os testes da Seção 5 desativados (`@Disabled`); helpers de leitura/comparação prontos e `resolucoesDe` marcado com TODO;
  - programas de `test/programas/escopo/` só com um comentário dizendo o que cada um deve cobrir (as saídas esperadas serão escritas à mão);
  - template de `escopo-design.md` com as seções pedidas no enunciado e atualização dos `README.md`.

### 2026-11-09 — Revisões e fechamento do CP4
- **Ferramenta:** Claude
- **Contexto/tarefa:** revisão do escopo-design.md, da tabela, da passada e dos testes (projetados e escritos por mim) e fechamento da entrega.
- **Prompt (resumo):** envio de cada parte para revisão; pedidos de correção de erros de compilação; no fim, a parte de fechamento.
- **O que foi gerado:**
  - revisão das seções 1 a 5 do escopo-design.md; reescrita do texto das seções 3 e 4 a pedido, mantendo as minhas decisões;
  - correções de compilação: tipo genérico da pilha no StackedSymbolTable; nomes de campo (id, block) no IdentificationVisitor e no ResolutionPrinter; a lógica dos dois visitors é minha;
  - SymbolTableTest: nó concreto no helper simbolo e assert do declare no teste de sombreamento; os testes são meus;
  - helper resolucoesDe do IdentificationTest (roda a passada e o ResolutionPrinter);
  - Main com o modo --escopo e README do CP4.

## Checkpoint 5 — Verificação de tipos

### 2026-11-21 — Estrutura de entrega do CP5 (boilerplate)
- **Ferramenta:** Claude
- **Contexto/tarefa:** criar a pasta do CP5 a partir do CP4 e os esqueletos da entrega, sem escrever as regras de tipagem, sem tomar as decisões e sem implementar o verificador.
- **Prompt (resumo):** enunciado do CP5; mesma divisão dos checkpoints anteriores: estrutura e boilerplate pela IA, projeto e implementação por mim. Escolhi o nome `types.Type` para o tipo semântico.
- **O que foi gerado:**
  - `cp5-PauloBorges/` como cópia do CP4 (código e testes sem modificação), `pom.xml` com artifactId/versão do CP5;
  - pacote `types/`: `Type` vazio (conteúdo a definir pela Parte 1) e `Types` só com as assinaturas de `binary`, `unary` e `assignable`; **nenhuma regra de tipo foi gerada**;
  - `TypeChecker` só com o esqueleto (métodos do `Visitor<Type>` vazios, listas de erros e de avisos com os getters); **nenhuma regra de checagem foi gerada**;
  - `TypesTest` e `TypeCheckerTest` com os testes da Seção 5 desativados (`@Disabled`); helpers de leitura, de execução das duas passadas e de comparação prontos;
  - programas de `test/programas/*.txt` só com um comentário dizendo o que cada um deve cobrir (as saídas esperadas serão escritas à mão);
  - atualização dos `README.md` (com as seções de representação, regras e mensagens marcadas como TODO).
- **O que aceitei / alterei:** <!-- TODO -->

### 2026-11-21 — Revisão das regras de tipos (Parte 1)
- **Ferramenta:** Claude
- **Contexto/tarefa:** revisar o rascunho digital da Parte 1 (regras de tipagem, tabela de operadores e as 11 decisões), feito por mim antes de passar ao papel.
- **Prompt (resumo):** "verifique se eu fiz certo; se houver algo errado, corrija; se recomendar outra decisão, pode trocar e eu avalio".
- **O que foi gerado:**
  - correções de forma: a linha de && e || da tabela, a coluna com o programa de teste de cada decisão
  - regras que faltavam: ID e ID-FUNC lendo x.decl, ERRO-PROPAGA (sem cascata), DECL-INIT, DECL, FUNC, BLOCO, a aridade (m = n) nas chamadas, retornaSempre e as regras de variáveis sem valor;
  - mudanças de decisão sugeridas: decisão 9 passou de "não checar" para a regra estrutural da Aula 13; read restrito a int, double e char; proposta de representação de double (ponto fixo) e string (comprimento + caracteres) na TAM.

### 2026-11-22 — Revisão dos programas de teste e das saídas esperadas
- **Ferramenta:** Claude
- **Contexto/tarefa:** revisar os programas de teste do CP5, escritos por mim a partir das regras da Parte 1, e as saídas esperadas.
- **Prompt (resumo):** envio dos programas e pergunta sobre o formato do arquivo esperado;
- **O que foi gerado:**
  - verificação, pelo parser, de que os programas são sintaticamente válidos;
  - decisao-argumentos: acrescentado um caso recusado (double passado para parâmetro int); valido-completo: acrescentada uma constante global;
  - o formato das mensagens de erro e de aviso
  - as saídas esperadas, derivadas das regras da Parte 1

### 2026-11-23 — Revisões da implementação (Parte 2)
- **Ferramenta:** Claude
- **Contexto/tarefa:** implementação do verificador em passos (expressões, comandos, return e chamadas, retornaSempre, variáveis sem valor, modo `--tipos`), com revisão de cada passo antes do seguinte.
- **Prompt (resumo):** envio do código e da saída do `mvn clean test` a cada passo; "qual o próximo passo?".
- **O que foi gerado:**
  - a ordem dos passos e, para cada um, a lista das regras da Parte 1 que ele cobre e os testes a ativar;
  - sugestões de projeto que eu segui: retornaSempre como um `Visitor<Boolean>` separado (o enunciado proíbe `instanceof`); o tipo declarado lido com `visitPrimitiveType` devolvendo `Types.of`; um helper só para `CallExpr` e `CallCmd`; o conjunto de variáveis sem valor guardando o nó da declaração e não o nome (por causa do sombreamento), com um conjunto separado para avisar uma vez só;
  - o texto de três mensagens que a Parte 1 não tinha fixado: atribuição a constante/função, read de tipo não legível e chamada de algo que não é função.

### 2026-11-23 — README do CP5
- **Ferramenta:** Claude
- **Contexto/tarefa:** escrever as seções do README pedidas na entrega.
- **Prompt (resumo):** "Faça o rascunho das seções do README a partir do código."
- **O que foi gerado:** rascunho de "Como os tipos estão representados", "Onde ficam as regras" (regra ou decisão → método) e "Formato das mensagens", e o parágrafo sobre o modo `--tipos`.
