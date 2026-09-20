# exemplo minimo da MiniLang (para testar o scanner)
const int limite = 1_000;
double taxa = 6.02e23;

int contador = 0;
while (contador < limite) {
    contador = contador + 1;
}

if (contador >= limite) {
    print("fim");
}
