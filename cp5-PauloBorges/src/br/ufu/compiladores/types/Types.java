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

    /** Tipo do resultado de "l op r", ou o tipo de erro se nao ha regra. */
    public static Type binary(String op, Type l, Type r) {
        // TODO: tabela de operadores binarios.
        throw new UnsupportedOperationException("TODO: Types.binary");
    }

    /** Tipo do resultado de "op t" (operadores unarios), ou o tipo de erro. */
    public static Type unary(String op, Type t) {
        // TODO: operadores unarios.
        throw new UnsupportedOperationException("TODO: Types.unary");
    }

    /** Um valor do tipo "from" pode ser guardado onde se espera "to"? */
    public static boolean assignable(Type to, Type from) {
        // TODO: compatibilidade e conversoes implicitas.
        throw new UnsupportedOperationException("TODO: Types.assignable");
    }
}
