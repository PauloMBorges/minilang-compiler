package br.ufu.compiladores.symbols;

/**
 * Categorias possíveis para um símbolo na tabela.
 * Diferencia as regras de uso na Análise Semântica (ex: proibir atribuição a CONST)
 */
public enum SymbolCategory {
    VAR,
    CONST,
    PARAM,
    FUNC,
    PROC
}