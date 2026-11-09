package br.ufu.compiladores.symbols;

/*
 * Erro da analise contextual: linha e mensagem (a mensagem ja traz o nome).
 * O texto de cada mensagem segue o formato documentado na secao 5 do
 * escopo-design.md.
 */
public record SemanticError(int line, String message) {

    @Override
    public String toString() {
        return "linha " + line + ": " + message;
    }
}
