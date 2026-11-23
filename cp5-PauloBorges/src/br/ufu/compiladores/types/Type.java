package br.ufu.compiladores.types;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Tipo semantico (Checkpoint 5, Aula 12).
 *
 * Nao confundir com br.ufu.compiladores.ast.Type, que e o no da AST que
 * representa o tipo escrito no programa. Este aqui e o tipo calculado pelo
 * verificador: o que fica em Symbol.type e no campo de tipo de cada expressao.
 */
public class Type {

    public static final Type INT = new Type("int");
    public static final Type DOUBLE = new Type("double");
    public static final Type BOOL = new Type("bool");
    public static final Type CHAR = new Type("char");
    public static final Type STRING = new Type("string");
    public static final Type VOID = new Type("void");
    public static final Type ERROR = new Type("erro");

    private final String name;
    
    public Type(String name) {
        this.name = name;
    }

    public boolean compatible(Type other) {
        return this == other;
    }

    @Override 
    public String toString() {
        return name;
    }

    // Assinatura de funcao ou procedimento: (P1, ..., Pn) -> Tret.
    public static class FuncType extends Type {
        public final List<Type> params;
        public final Type returnType;

        public FuncType(List<Type> params, Type returnType) {
            super(formatSignature(params, returnType));
            this.params = List.copyOf(params);
            this.returnType = returnType;
        }

        private static String formatSignature(List<Type> params, Type returnType) {
            String ps = params.stream()
                .map(Type::toString)
                .collect(Collectors.joining(", "));
            return "(" + ps + ") -> " + returnType;
        }
    }

}
