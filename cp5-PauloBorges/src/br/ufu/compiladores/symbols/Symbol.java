package br.ufu.compiladores.symbols;

import br.ufu.compiladores.ast.Node;
import br.ufu.compiladores.ast.PrimitiveType;

/*
 * Entrada da tabela de simbolos (Checkpoint 4).
 */
public class Symbol {

    // Campos inicializados na criação (Fase de Escopo)
    public final String name;
    public final SymbolCategory category;
    public final Node declNode;
    public final int level;

    // Campos reservados para as próximas fases (fora do construtor)
    public PrimitiveType type = null; // Preenchido no CP5 (Análise Semântica)
    public int offset = -1;           // Preenchido na Geração de Código

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