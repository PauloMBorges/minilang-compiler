package br.ufu.compiladores.types;

/*
 * A tabela de operadores e a compatibilidade em Java (Checkpoint 5).
 *
 * Cada metodo traduz uma parte da Parte 1 (tipos-a-mao.pdf): o Checker so
 * chama estes metodos, e os testes de TypesTest os exercitam sem passar
 * pelo parser. Nenhum metodo reporta erro: devolvem o tipo do resultado
 * (ou o tipo de erro), e quem reporta e o verificador.
 */
public final class Types {

    private Types() {}

    public static boolean numeric(Type t) {
        return t == Type.INT || t == Type.DOUBLE;
    }

    public static boolean comparableByEquality(Type t) {
        return t == Type.INT || t == Type.DOUBLE || t == Type.BOOL 
            || t == Type.CHAR || t == Type.STRING;
    }

    // converte o nome do tipo escrito na declaracao ("int") no Type semantico
    public static Type of(String name) {
        switch (name) {
            case "int":    return Type.INT;
            case "double": return Type.DOUBLE;
            case "bool":   return Type.BOOL;
            case "char":   return Type.CHAR;
            case "string": return Type.STRING;
            case "void":   return Type.VOID;
            default:       return Type.ERROR;
        }
    }

    /** Tipo do resultado de "l op r", ou o tipo de erro se nao ha regra. */
    public static Type binary(String op, Type l, Type r) {
        if (l == Type.ERROR || r == Type.ERROR) return Type.ERROR;

        boolean nums = numeric(l) && numeric(r);
        Type wide = !nums ? Type.ERROR 
                : (l == Type.DOUBLE || r == Type.DOUBLE ? Type.DOUBLE : Type.INT);

        switch (op) {
            case "+":
                if (l == Type.STRING && r == Type.STRING) return Type.STRING;
                return wide;
            case "-":
            case "*":
            case "/":
            case "**":
                return wide;
            case "%":
                return (l == Type.INT && r == Type.INT) ? Type.INT : Type.ERROR;
            case "<":
            case "<=":
            case ">":
            case ">=":
                return (nums || (l == Type.CHAR && r == Type.CHAR)) ? Type.BOOL : Type.ERROR;
            case "==":
            case "!=":
                return (nums || (comparableByEquality(l) && l.compatible(r)))
                        ? Type.BOOL : Type.ERROR;
            case "&&":
            case "||":
                return (l == Type.BOOL && r == Type.BOOL) ? Type.BOOL : Type.ERROR;
            default:
                return Type.ERROR;
        }
    }

    /** Tipo do resultado de "op t" (operadores unarios), ou o tipo de erro. */
    public static Type unary(String op, Type t) {
        if (t == Type.ERROR) return Type.ERROR;

        switch (op) {
            case "-": return numeric(t) ? t : Type.ERROR;
            case "!": return t == Type.BOOL ? Type.BOOL : Type.ERROR;
            default:  return Type.ERROR;
        }
    }

    /** Um valor do tipo "from" pode ser guardado onde se espera "to"? */
    public static boolean assignable(Type to, Type from) {
        return to.compatible(from)
                || (to == Type.DOUBLE && from == Type.INT)
                || to == Type.ERROR
                || from == Type.ERROR;
    }
}
