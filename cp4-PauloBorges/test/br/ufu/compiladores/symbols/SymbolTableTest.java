package br.ufu.compiladores.symbols;

import br.ufu.compiladores.ast.IdExpr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 4: a tabela de simbolos isolada (Secao 5 do enunciado).
 *
 * Os testes usam so a interface SymbolTable; a classe concreta aparece apenas
 * em novaTabela().
 */
public class SymbolTableTest {

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------

    /** Tabela nova, ja com o escopo global aberto (level() == 0). */
    private static SymbolTable novaTabela() {
        return new StackedSymbolTable();
    }

    /** Symbol de teste com o nome dado, no nivel atual da tabela. */
    private static Symbol simbolo(String nome, SymbolTable t) {
        // VAR como categoria padrão e um IdExpr qualquer como nó: a tabela só
        // olha o nome, mas o toString() do Symbol (usado nas mensagens de falha)
        // precisa de um nó para ler a linha.
        return new Symbol(nome, SymbolCategory.VAR, new IdExpr(1, 1, nome), t.level());
    }

    // ------------------------------------------------------------------
    //  CONTRATO DA TABELA
    // ------------------------------------------------------------------

    /** declare devolve true e lookup / lookupLocal acham o mesmo simbolo. */
    @Test 
    void declararEBuscar() {
        SymbolTable t = novaTabela();
        Symbol s = simbolo("x", t);
        
        assertTrue(t.declare(s), "A declaração deve retornar true para um nome novo");
        
        assertSame(s, t.lookup("x"), "lookup deve retornar a exata mesma instância do símbolo");
        assertSame(s, t.lookupLocal("x"), "lookupLocal deve retornar a exata mesma instância do símbolo no escopo atual");
    }

    /** nome interno oculta o externo; depois do closeScope, o externo volta. */
    @Test 
    void sombreamentoERestauracao() {
        SymbolTable t = novaTabela();
        Symbol globalX = simbolo("x", t);
        t.declare(globalX);
        
        t.openScope(); // Entra em um bloco interno
        Symbol localX = simbolo("x", t);
        assertTrue(t.declare(localX), "Sombrear em escopo interno deve ser aceito");
        
        assertSame(localX, t.lookup("x"), "No escopo interno, lookup deve achar o x local");
        
        t.closeScope(); // Sai do bloco
        
        assertSame(globalX, t.lookup("x"), "Após fechar o escopo, o x global deve voltar a ser visível");
    }

    /** segunda declaracao no mesmo escopo: declare devolve false e o primeiro simbolo permanece. */
    @Test 
    void redeclaracaoMantemOPrimeiro() {
        SymbolTable t = novaTabela();
        Symbol s1 = simbolo("x", t);
        Symbol s2 = simbolo("x", t); // Outra instância com o mesmo nome
        
        assertTrue(t.declare(s1), "A primeira declaração deve funcionar");
        assertFalse(t.declare(s2), "A segunda declaração no mesmo escopo deve falhar (retornar false)");
        
        assertSame(s1, t.lookup("x"), "A tabela deve manter o primeiro símbolo declarado");
    }

    /** lookupLocal nao enxerga o escopo externo (lookup enxerga). */
    @Test 
    void lookupLocalNaoVeEscopoExterno() {
        SymbolTable t = novaTabela();
        Symbol globalY = simbolo("y", t);
        t.declare(globalY);
        
        t.openScope(); // Abre bloco interno
        
        assertSame(globalY, t.lookup("y"), "lookup normal enxerga o escopo externo");
        assertNull(t.lookupLocal("y"), "lookupLocal NÃO deve enxergar o escopo externo, apenas o topo");
    }

    /** lookup e lookupLocal de nome inexistente devolvem null, sem excecao. */
    @Test 
    void nomeInexistenteDevolveNull() {
        SymbolTable t = novaTabela();
        
        assertNull(t.lookup("nao_existo"));
        assertNull(t.lookupLocal("nao_existo"));
    }

    /** level() acompanha openScope / closeScope (0 = global). */
    @Test 
    void levelAcompanhaOsEscopos() {
        SymbolTable t = novaTabela();
        assertEquals(0, t.level(), "Tabela nova deve começar no level 0 (global)");
        
        t.openScope();
        assertEquals(1, t.level(), "Após openScope, o level deve ser 1");
        
        t.openScope();
        assertEquals(2, t.level(), "Após outro openScope, o level deve ser 2");
        
        t.closeScope();
        assertEquals(1, t.level(), "Após closeScope, o level deve voltar para 1");
        
        t.closeScope();
        assertEquals(0, t.level(), "Após outro closeScope, o level deve voltar para 0");
    }

    /** closeScope sem escopo aberto: o comportamento documentado na secao 3 do escopo-design.md. */
    @Test 
    void closeScopeSemEscopoAberto() {
        SymbolTable t = novaTabela();
        
        // A tabela já começa no level 0. Tentar dar closeScope no global deve lançar IllegalStateException.
        assertThrows(IllegalStateException.class, () -> {
            t.closeScope();
        }, "Fechar o escopo global (sem ter aberto outros antes) deve lançar IllegalStateException");
    }
}