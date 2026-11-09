package br.ufu.compiladores.symbols;

/*
 * API da tabela de simbolos (Checkpoint 4, Aula 11). As assinaturas sao as do
 * enunciado e nao podem ser alteradas.
 *
 * Contrato (Secao 3 do enunciado):
 *  - declare nao substitui um simbolo existente: devolve false e a primeira
 *    declaracao permanece;
 *  - lookup e lookupLocal devolvem null quando nao acham; nunca lancam excecao;
 *  - closeScope sem escopo aberto: comportamento documentado em
 *    escopo-design.md, nunca uma NullPointerException sem explicacao.
 *
 */
public interface SymbolTable {

    /** Entra em um bloco. */
    void openScope();

    /** Sai do bloco. */
    void closeScope();

    /** Declara no escopo do topo. false: o nome ja existe neste escopo. */
    boolean declare(Symbol s);

    /** Busca so no escopo do topo; null se nao achar. */
    Symbol lookupLocal(String name);

    /** Busca do escopo mais interno ao mais externo; null se nao achar. */
    Symbol lookup(String name);

    /** Profundidade atual (0 = global). */
    int level();
}