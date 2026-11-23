# valido03.ml: Funcoes, procedimentos, retorno e chamadas
int somar(int a, int b) {
    return a + b;
}

void imprimirSoma(int a, int b) {
    int resultado;
    resultado = somar(a, b);
    print(resultado);
    return; # return vazio permitido em void
}

void main() {
    imprimirSoma(10, 20);
}