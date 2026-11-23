package br.ufu.compiladores.visitor;

import br.ufu.compiladores.ast.*;
import br.ufu.compiladores.symbols.SemanticError;
import br.ufu.compiladores.types.Type;   // tipo semantico (nao o ast.Type)

import java.util.ArrayList;
import java.util.List;

/*
 * Verificador de tipos (Checkpoint 5, Secao 4 do enunciado).
 *
 * Roda depois da passada de identificacao (CP4), sobre a AST ja decorada com
 * decl. Calcula o tipo de cada expressao (gravado no no), confere comandos,
 * return e chamadas e acumula os erros e os avisos.
 *
 * Regras do enunciado:
 *  - sem instanceof nem switch sobre o tipo do no;
 *  - nao faz declare, openScope nem closeScope: le a assinatura no Symbol;
 *  - sem cascata: depois do primeiro erro, o tipo de erro sobe e e
 *    compativel com tudo, sem nova mensagem;
 *  - variaveis sem valor: analise conservadora, reportada como aviso;
 *  - nenhuma entrada causa NullPointerException nem laco infinito.
 *
 * As regras e as decisoes estao na Parte 1 (tipos-a-mao.pdf); a tabela de
 * operadores esta em types/Types.java.
 */
public class TypeChecker implements Visitor<Type> {

    private final List<SemanticError> errors = new ArrayList<>();
    private final List<SemanticError> warnings = new ArrayList<>();

    // TODO: contexto (funcao corrente, variaveis que podem estar sem valor...).

    /** Erros de tipo, na ordem em que foram encontrados. */
    public List<SemanticError> getErrors() {
        return errors;
    }

    /** Avisos (variaveis que podem estar sem valor). */
    public List<SemanticError> getWarnings() {
        return warnings;
    }

    /** Registra um erro; o formato das mensagens esta no README. */
    private void erro(int line, String message) {
        errors.add(new SemanticError(line, message));
    }

    /** Registra um aviso: "linha N: aviso: mensagem". */
    private void aviso(int line, String message) {
        warnings.add(new SemanticError(line, "aviso: " + message));
    }

    // --- Programa e declaracoes ---

    @Override public Type visitProgram(Program n) { /* TODO */ return null; }
    @Override public Type visitVarDecl(VarDecl n) { /* TODO */ return null; }
    @Override public Type visitFuncDecl(FuncDecl n) { /* TODO */ return null; }
    @Override public Type visitProcDecl(ProcDecl n) { /* TODO */ return null; }
    @Override public Type visitParam(Param n) { /* TODO */ return null; }

    // --- Tipos ---

    @Override public Type visitPrimitiveType(PrimitiveType n) { /* TODO */ return null; }

    // --- Comandos ---

    @Override public Type visitBlock(BlockCmd n) { /* TODO */ return null; }
    @Override public Type visitDeclCmd(DeclCmd n) { /* TODO */ return null; }
    @Override public Type visitAssign(AssignCmd n) { /* TODO */ return null; }
    @Override public Type visitIf(IfCmd n) { /* TODO */ return null; }
    @Override public Type visitElif(ElifBranch n) { /* TODO */ return null; }
    @Override public Type visitWhile(WhileCmd n) { /* TODO */ return null; }
    @Override public Type visitCallCmd(CallCmd n) { /* TODO */ return null; }
    @Override public Type visitReturn(ReturnCmd n) { /* TODO */ return null; }
    @Override public Type visitPrint(PrintCmd n) { /* TODO */ return null; }
    @Override public Type visitRead(ReadCmd n) { /* TODO */ return null; }

    // --- Expressoes ---

    @Override public Type visitIntLiteral(IntLiteral n) { /* TODO */ return null; }
    @Override public Type visitDoubleLiteral(DoubleLiteral n) { /* TODO */ return null; }
    @Override public Type visitBoolLiteral(BoolLiteral n) { /* TODO */ return null; }
    @Override public Type visitCharLiteral(CharLiteral n) { /* TODO */ return null; }
    @Override public Type visitStringLiteral(StringLiteral n) { /* TODO */ return null; }
    @Override public Type visitId(IdExpr n) { /* TODO */ return null; }
    @Override public Type visitBin(BinExpr n) { /* TODO */ return null; }
    @Override public Type visitUn(UnExpr n) { /* TODO */ return null; }
    @Override public Type visitCallExpr(CallExpr n) { /* TODO */ return null; }
}
