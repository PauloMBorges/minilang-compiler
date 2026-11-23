package br.ufu.compiladores.symbols;

import br.ufu.compiladores.ast.Node;
import br.ufu.compiladores.types.Type;

/*
 * Entrada da tabela de simbolos (Checkpoint 4 / Checkpoint 5).
 */
public class Symbol {

    // Campos inicializados na criação (Fase de Escopo)
    public final String name;
    public final SymbolCategory category;
    public final Node declNode;
    public final int level;

    // Campos preenchidos nas fases seguintes
    public Type type = null; // Preenchido no CP5 (Identificação / Análise Semântica)
    public int offset = -1;  // Preenchido na Geração de Código

    public Symbol(String name, SymbolCategory category, Node declNode, int level) {
        this.name = name;
        this.category = category;
        this.declNode = declNode;
        this.level = level;
    }

    @Override
    public String toString() {
        return name + "@" + declNode.line();
    }
}