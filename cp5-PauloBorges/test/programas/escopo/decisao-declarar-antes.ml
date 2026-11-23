# Decisao 2: Declarar antes de usar (a regra "resolve uso, depois declara")
int a = a; # ERRO: 'a' do lado direito ainda nao foi declarado

int x = 10;

void main() {
    int x = x; # OK: 'x' do lado direito resolve para o 'x' global (linha 4)
    print(x);
    
    y = 5; # ERRO: 'y' usado antes da declaracao
    int y;
}