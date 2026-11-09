# procedimento-recursivo.ml
void loop(int n) {
    if (n > 0) {
        print(n);
        loop(n - 1); # OK: o nome 'loop' ja esta no ambiente
    }
}