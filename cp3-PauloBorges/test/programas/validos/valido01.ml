# valido01.ml: Tipos basicos, variaveis, constantes e expressoes matematicas
# java -cp target/classes br.ufu.compiladores.Main test/programas/validos/valido01.ml
const double PI = 3.14159;

void main() {
    int a;
    double b;
    string nome = "Compiladores";
    char inicial = 'P';
    
    a = 10;
    # Teste de precedencia e associatividade a direita (2 ** 3 eh resolvido primeiro)
    int c = a + 5 * 2 ** 2 ** 3; 

    # Teste de operadores unarios, parenteses, logicos, igualdade, modulo e divisao
    bool ok = !(a != 10) || -a < 0;
    int d = (a + 1) % 3 / 2;
    
    print(c);
}