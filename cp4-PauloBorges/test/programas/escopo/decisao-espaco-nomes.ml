# Decisao 5: Espaco de nomes unico
int f = 1;
void f() { } # ERRO: redeclaracao. 'f' ja existe como variavel global

int g() {
    int g = 1; # OK: variavel local sombreia a funcao global 'g'
    g(); # Resolve para a variavel local 'g'. (O CP5 vai acusar "nao eh funcao")
    return g;
}