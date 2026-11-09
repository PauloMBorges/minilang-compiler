# Decisao 3: Funcoes mutuamente recursivas
void ping(int n) {
    if (n > 0) {
        pong(n - 1); # ERRO: 'pong' ainda nao foi declarada (recursao mutua falha)
    }
    ping(n - 1); # OK: recursao direta funciona, pois 'ping' ja esta declarada
}

void pong(int n) {
    if (n > 0) {
        ping(n - 1); # OK: 'ping' ja foi declarada acima
    }
}