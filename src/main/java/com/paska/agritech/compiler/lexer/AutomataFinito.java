package com.paska.agritech.compiler.lexer;

/**
 * AUTOMATA FINITO DETERMINISTA (AFD)  -  Tema del curso.
 *
 * Reconoce numeros validos (enteros o decimales) usados en las consultas y
 * tambien valida campos del modelo de datos (p. ej. DNI de 8 digitos).
 *
 * Se implementa explicitamente como una maquina de estados con su funcion
 * de transicion delta(estado, simbolo) -> estado, demostrando el concepto
 * formal de AFD = (Q, Sigma, delta, q0, F).
 *
 *  AFD para NUMERO (entero o decimal):
 *
 *     [q0] --digito--> [q1]* --digito--> [q1]*
 *      |                 |
 *      |                 '--punto--> [q2] --digito--> [q3]* --digito--> [q3]*
 *
 *   Estados de aceptacion (F): q1 (entero) y q3 (decimal).
 */
public class AutomataFinito {

    private enum Estado { Q0, Q1, Q2, Q3, RECHAZO }

    /**
     * Funcion de transicion + recorrido de la cadena.
     * @return true si la cadena es un numero entero o decimal valido.
     */
    public boolean esNumero(String cadena) {
        if (cadena == null || cadena.isEmpty()) return false;

        Estado estado = Estado.Q0;   // q0: estado inicial
        for (char c : cadena.toCharArray()) {
            estado = transicion(estado, c);
            if (estado == Estado.RECHAZO) return false;
        }
        // Acepta solo si termina en un estado final (Q1 entero o Q3 decimal).
        return estado == Estado.Q1 || estado == Estado.Q3;
    }

    /** delta: funcion de transicion del AFD. */
    private Estado transicion(Estado actual, char c) {
        boolean digito = Character.isDigit(c);
        boolean punto = (c == '.');

        return switch (actual) {
            case Q0 -> digito ? Estado.Q1 : Estado.RECHAZO;
            case Q1 -> digito ? Estado.Q1 : (punto ? Estado.Q2 : Estado.RECHAZO);
            case Q2 -> digito ? Estado.Q3 : Estado.RECHAZO;
            case Q3 -> digito ? Estado.Q3 : Estado.RECHAZO;
            default -> Estado.RECHAZO;
        };
    }

    /**
     * AFD especializado: valida un DNI peruano = exactamente 8 digitos.
     * Q0 -> Q1 -> ... -> Q8(aceptacion). Mas de 8 digitos => rechazo.
     */
    public boolean esDNI(String cadena) {
        if (cadena == null) return false;
        int estado = 0;                 // numero de digitos leidos
        for (char c : cadena.toCharArray()) {
            if (!Character.isDigit(c)) return false; // simbolo fuera del alfabeto
            estado++;
            if (estado > 8) return false;            // estado de rechazo
        }
        return estado == 8;             // unico estado de aceptacion
    }
}
