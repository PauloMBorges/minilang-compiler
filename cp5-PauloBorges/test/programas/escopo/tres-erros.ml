# tres-erros.ml
int x = 1;
int x = 2; # ERRO 1 (redeclaracao global)

void main() {
    print(y); # ERRO 2 (nao declarado)
    
    if (x > 0) {
        int z = 10;
        int z = 20; # ERRO 3 (redeclaracao local)
    }
}