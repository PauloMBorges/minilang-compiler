# Saídas esperadas

Cada arquivo `.txt` desta pasta é a saída **esperada** de um programa ou expressão de teste. O `AstTest` (CP3), o `IdentificationTest` (CP4) e o `TypeCheckerTest` (CP5) comparam a saída real com o arquivo.

**Os arquivos são escritos à mão, antes de rodar o impressor ou a passada** (Seção 5 do enunciado). Copiar a saída do programa para cá faz o teste passar sem testar nada.

| Arquivo | Entrada | Teste |
|---|---|---|
| `valido01.txt` | `test/programas/validos/valido01.ml` | `arvoreValido01` |
| `valido02.txt` | `test/programas/validos/valido02.ml` | `arvoreValido02` |
| `valido03.txt` | `test/programas/validos/valido03.ml` | `arvoreValido03` |
| `prec-soma-mult.txt` | `a + b * c` | `precedenciaDiferente` |
| `assoc-sub.txt` | `a - b - c` | `associatividadeEsquerda` |
| `parenteses-sub.txt` | `a - (b - c)` | `parentesesMudamArvore` |

### Checkpoint 4 — passada de identificação

Cada arquivo tem o mesmo nome do programa em `test/programas/escopo/`. Conteúdo: as resoluções dos usos (uma por linha) e, depois, os erros (um por linha), na ordem do texto, nos formatos da seção 5 do `escopo-design.md`. Escritos a partir do desenho da seção 1, antes de rodar a passada.

| Arquivo | Teste |
|---|---|
| `sombreamento.txt` | `validoComSombreamento` |
| `redeclaracao.txt` | `redeclaracao` |
| `nao-declarado.txt` | `naoDeclarado` |
| `decisao-sombreamento.txt` | `decisaoSombreamento` |
| `decisao-declarar-antes.txt` | `decisaoDeclararAntesDeUsar` |
| `decisao-recursao-mutua.txt` | `decisaoRecursaoMutua` |
| `decisao-parametros-corpo.txt` | `decisaoParametrosECorpo` |
| `decisao-espaco-nomes.txt` | `decisaoEspacoDeNomes` |
| `tres-erros.txt` | `tresErrosNaOrdem` |
| `procedimento-recursivo.txt` | `procedimentoRecursivo` |
| `uso-apos-bloco.txt` | `usoDepoisDoBloco` |

A comparação ignora a diferença entre `\r\n` e `\n` e as linhas em branco no início e no fim do arquivo.

### Checkpoint 5 — verificação de tipos

Cada arquivo tem o mesmo nome do programa em `test/programas/` (os dois com extensão `.txt`, como pede o enunciado). Conteúdo: os erros da identificação, os erros de tipo e os avisos, um por linha, ordenados pela linha do programa, nos formatos do `README.md`. Escritos a partir das regras da Parte 1 (`tipos-a-mao.pdf`), antes de rodar o verificador. Programa válido: arquivo vazio.

| Arquivo | Teste |
|---|---|
| `valido-completo.txt` | `validoCompleto` |
| `erro-expressao.txt` | `erroEmExpressao` |
| `cond_int.txt` | `condicaoNaoBooleana` |
| `return-incompativel.txt` | `returnIncompativel` |
| `chamada-errada.txt` | `chamadaErrada` |
| `proc-como-expressao.txt` | `procedimentoComoExpressao` |
| `varios-erros.txt` | `variosErrosSemCascata` |
| `funcao-recursiva.txt` | `funcaoRecursiva` |
| `recursao-mutua.txt` | `recursaoMutua` (decisão 7) |
| `sem-valor.txt` | `variaveisSemValor` |
| `decisao-int-double.txt` | `decisaoIntDouble` |
| `decisao-char-int.txt` | `decisaoCharInt` |
| `decisao-bool-aritmetica.txt` | `decisaoBoolAritmetica` |
| `decisao-string.txt` | `decisaoString` |
| `decisao-igualdade-mista.txt` | `decisaoIgualdadeMista` |
| `decisao-funcao-comando.txt` | `decisaoFuncaoComoComando` |
| `decisao-argumentos.txt` | `decisaoArgumentos` |
| `decisao-return-caminhos.txt` | `decisaoReturnCaminhos` |
| `decisao-sobrecarga.txt` | `decisaoSobrecarga` |
| `decisao-funcao-sem-parenteses.txt` | `decisaoFuncaoSemParenteses` |

