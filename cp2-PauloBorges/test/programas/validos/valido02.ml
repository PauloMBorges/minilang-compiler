# valido02.ml: Estruturas de controle e repeticao
void main() {
    int x = 0;
    read(x);
    
    # if/elif/else com chaves obrigatorias (sem dangling-else)
    if (x > 20) {
        print("Acima do intervalo");
    } elif (x > 10 && x <= 20) {
        print("Dentro do intervalo");
    } else {
        print("Abaixo do intervalo");
    }
    
    bool executando = true;
    while (executando) {
        x = x - 1;
        if (x == 0) {
            executando = false;
        }
    }
}