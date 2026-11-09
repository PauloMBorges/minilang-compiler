# uso-apos-bloco.ml
void main() {
    if (true) {
        int interna = 42;
        print(interna); # OK
    }
    print(interna); # ERRO: nao declarado fora do bloco
}