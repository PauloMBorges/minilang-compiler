package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;
import br.ufu.compiladores.symbols.SemanticError;
import br.ufu.compiladores.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/*
 * Passada de identificacao (Checkpoint 4, Secao 4 do enunciado).
 *
 * Percorre a AST do CP3 (so roda sobre AST valida), abre e fecha escopos,
 * declara os nomes e decora a arvore: cada identificador de uso passa a
 * apontar para o Symbol da sua declaracao (campo decl).
 *
 * Regras do enunciado:
 *  - sem instanceof nem switch sobre o tipo do no;
 *  - depende so da interface SymbolTable (a classe concreta aparece apenas
 *    na linha que cria a tabela);
 *  - acumula todos os erros e continua; nunca lanca NullPointerException nem
 *    entra em laco infinito;
 *  - ao final, a lista de erros fica acessivel em getErrors().
 */
public class IdentificationVisitor implements Visitor<Void> {

    private final SymbolTable table;
    private final List<SemanticError> errors = new ArrayList<>();

    public IdentificationVisitor() {
        // TODO: criar a implementacao escolhida (secao 3 do escopo-design.md).
        //       Unica linha da passada que conhece a classe concreta.
        this.table = null;
    }

    /** Erros na ordem em que foram encontrados (ordem do texto). */
    public List<SemanticError> getErrors() {
        return errors;
    }

    /** Registra um erro; o formato da mensagem esta na secao 5 do escopo-design.md. */
    private void erro(int line, String message) {
        errors.add(new SemanticError(line, message));
    }

    // --- Programa e declaracoes ---

    @Override public Void visitProgram(Program n) { /* TODO */ return null; }
    @Override public Void visitVarDecl(VarDecl n) { /* TODO */ return null; }
    @Override public Void visitFuncDecl(FuncDecl n) { /* TODO */ return null; }
    @Override public Void visitProcDecl(ProcDecl n) { /* TODO */ return null; }
    @Override public Void visitParam(Param n) { /* TODO */ return null; }

    // --- Tipos ---

    @Override public Void visitPrimitiveType(PrimitiveType n) { /* TODO */ return null; }

    // --- Comandos ---

    @Override public Void visitBlock(BlockCmd n) { /* TODO */ return null; }
    @Override public Void visitDeclCmd(DeclCmd n) { /* TODO */ return null; }
    @Override public Void visitAssign(AssignCmd n) { /* TODO */ return null; }
    @Override public Void visitIf(IfCmd n) { /* TODO */ return null; }
    @Override public Void visitElif(ElifBranch n) { /* TODO */ return null; }
    @Override public Void visitWhile(WhileCmd n) { /* TODO */ return null; }
    @Override public Void visitCallCmd(CallCmd n) { /* TODO */ return null; }
    @Override public Void visitReturn(ReturnCmd n) { /* TODO */ return null; }
    @Override public Void visitPrint(PrintCmd n) { /* TODO */ return null; }
    @Override public Void visitRead(ReadCmd n) { /* TODO */ return null; }

    // --- Expressoes ---

    @Override public Void visitIntLiteral(IntLiteral n) { /* TODO */ return null; }
    @Override public Void visitDoubleLiteral(DoubleLiteral n) { /* TODO */ return null; }
    @Override public Void visitBoolLiteral(BoolLiteral n) { /* TODO */ return null; }
    @Override public Void visitCharLiteral(CharLiteral n) { /* TODO */ return null; }
    @Override public Void visitStringLiteral(StringLiteral n) { /* TODO */ return null; }
    @Override public Void visitId(IdExpr n) { /* TODO */ return null; }
    @Override public Void visitBin(BinExpr n) { /* TODO */ return null; }
    @Override public Void visitUn(UnExpr n) { /* TODO */ return null; }
    @Override public Void visitCallExpr(CallExpr n) { /* TODO */ return null; }
}
