# Árvores esperadas

Cada arquivo `.txt` desta pasta é a saída **esperada** do impressor de árvore para um programa ou expressão de teste. O `AstTest` compara a saída real com o arquivo.

**Os arquivos são escritos à mão, antes de rodar o impressor** (Seção 5 do enunciado). Copiar a saída do programa para cá faz o teste passar sem testar nada.

| Arquivo | Entrada | Teste |
|---|---|---|
| `valido01.txt` | `test/programas/validos/valido01.ml` | `arvoreValido01` |
| `valido02.txt` | `test/programas/validos/valido02.ml` | `arvoreValido02` |
| `valido03.txt` | `test/programas/validos/valido03.ml` | `arvoreValido03` |
| `prec-soma-mult.txt` | `a + b * c` | `precedenciaDiferente` |
| `assoc-sub.txt` | `a - b - c` | `associatividadeEsquerda` |
| `parenteses-sub.txt` | `a - (b - c)` | `parentesesMudamArvore` |

A comparação ignora a diferença entre `\r\n` e `\n` e as linhas em branco no início e no fim do arquivo.
