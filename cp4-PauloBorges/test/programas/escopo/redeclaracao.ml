# redeclaracao.ml
int a = 1;
int a = 2; # ERRO: 'a' ja declarado

void main() {
    print(a); # Resolve para o da linha 2
}