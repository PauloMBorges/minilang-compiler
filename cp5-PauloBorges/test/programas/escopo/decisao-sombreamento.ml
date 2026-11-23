# Decisao 1: Sombreamento e redeclaracao
int x = 10;

void main() {
    int x = 20; # OK: sombreamento do 'x' global eh permitido
    print(x);
    int x = 30; # ERRO: redeclaracao de 'x' no exato mesmo bloco
}