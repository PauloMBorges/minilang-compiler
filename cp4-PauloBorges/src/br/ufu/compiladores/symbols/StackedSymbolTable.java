package br.ufu.compiladores.symbols;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementação da Tabela de Símbolos usando uma Pilha de Mapas.
 * Conforme definido na seção 3 do escopo-design.md.
 */
public class StackedSymbolTable implements SymbolTable {

    // A pilha que armazena os escopos: o topo é o escopo mais interno.
    private final Deque<Map<String, Symbol>> scopes;

    public StackedSymbolTable() {
        this.scopes = new ArrayDeque<>();
        // O construtor já abre o escopo global (level == 0)
        openScope();
    }

    @Override
    public void openScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void closeScope() {
        // Se a pilha tem apenas 1 elemento, estamos no escopo global.
        // Tentar fechar o global ou chamar sem escopo aberto é um erro interno do compilador.
        if (scopes.size() <= 1) {
            throw new IllegalStateException("closeScope sem escopo aberto");
        }
        scopes.pop();
    }

    @Override
    public boolean declare(Symbol s) {
        Map<String, Symbol> currentScope = scopes.peek();
        
        // Declare não substitui um símbolo existente: devolve false e a primeira permanece.
        if (currentScope.containsKey(s.name)) {
            return false; 
        }
        
        currentScope.put(s.name, s);
        return true;
    }

    @Override
    public Symbol lookupLocal(String name) {
        // Busca apenas no escopo do topo
        return scopes.peek().get(name);
    }

    @Override
    public Symbol lookup(String name) {
        // O iterador do ArrayDeque percorre a pilha do topo (mais interno) 
        // para a base (mais externo/global).
        for (Map<String, Symbol> scope : scopes) {
            Symbol s = scope.get(name);
            if (s != null) {
                return s;
            }
        }
        return null; // Retorna null se não encontrar em nenhum escopo
    }

    @Override
    public int level() {
        // A profundidade é o tamanho da pilha menos 1.
        // Se só tem o global (1 mapa), o level é 0.
        return scopes.size() - 1;
    }
}