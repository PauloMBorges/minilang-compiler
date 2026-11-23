void main() {
    int a = 10
    print(a);
}

# invalido01 - um unico erro sintatico (ex.: ';' faltando).
#   Erro na linha 3, coluna 5: "Token inesperado apos expressao de potencia: PRINT" (o ';' que falta só é percebido no token seguinte)
# java -cp target/classes br.ufu.compiladores.Main test/programas/invalidos/invalido01.ml