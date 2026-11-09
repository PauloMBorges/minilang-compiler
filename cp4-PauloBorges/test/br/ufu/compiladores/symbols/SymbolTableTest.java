package br.ufu.compiladores.symbols;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Suite do Checkpoint 4: a tabela de simbolos isolada (Secao 5 do enunciado).
 *
 * Os testes usam so a interface SymbolTable; a classe concreta aparece apenas
 * em novaTabela(). Tudo desativado (@Disabled) ate Symbol e a implementacao
 * existirem.
 */
public class SymbolTableTest {

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------

    /** Tabela nova, ja com o escopo global aberto (level() == 0). */
    private static SymbolTable novaTabela() {
        // TODO: unica linha que conhece a classe concreta escolhida.
        throw new UnsupportedOperationException("TODO: novaTabela");
    }

    /** Symbol de teste com o nome dado, no nivel atual da tabela. */
    private static Symbol simbolo(String nome, SymbolTable t) {
        // TODO: depende do construtor de Symbol (secao 2 do escopo-design.md).
        throw new UnsupportedOperationException("TODO: simbolo");
    }

    // ------------------------------------------------------------------
    //  CONTRATO DA TABELA
    // ------------------------------------------------------------------

    /** declare devolve true e lookup / lookupLocal acham o mesmo simbolo. */
    @Disabled("TODO") @Test void declararEBuscar() {
        // TODO
    }

    /** nome interno oculta o externo; depois do closeScope, o externo volta. */
    @Disabled("TODO") @Test void sombreamentoERestauracao() {
        // TODO
    }

    /** segunda declaracao no mesmo escopo: declare devolve false e o primeiro simbolo permanece. */
    @Disabled("TODO") @Test void redeclaracaoMantemOPrimeiro() {
        // TODO
    }

    /** lookupLocal nao enxerga o escopo externo (lookup enxerga). */
    @Disabled("TODO") @Test void lookupLocalNaoVeEscopoExterno() {
        // TODO
    }

    /** lookup e lookupLocal de nome inexistente devolvem null, sem excecao. */
    @Disabled("TODO") @Test void nomeInexistenteDevolveNull() {
        // TODO
    }

    /** level() acompanha openScope / closeScope (0 = global). */
    @Disabled("TODO") @Test void levelAcompanhaOsEscopos() {
        // TODO
    }

    /** closeScope sem escopo aberto: o comportamento documentado na secao 3 do escopo-design.md. */
    @Disabled("TODO") @Test void closeScopeSemEscopoAberto() {
        // TODO
    }
}
