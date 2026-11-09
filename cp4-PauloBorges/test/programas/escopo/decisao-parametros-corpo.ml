# Decisao 4: Parametros e corpo no mesmo escopo
void f(int a) {
    int a = 1; # ERRO: redeclaracao. 'a' ja existe no escopo (como parametro)
    print(a);
}

void main() {
    f(10);
}