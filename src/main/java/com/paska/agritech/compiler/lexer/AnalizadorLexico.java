package com.paska.agritech.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ANALIZADOR LEXICO (Scanner)  -  Tema del curso.
 *
 * Convierte la cadena de entrada en una lista de tokens. Combina dos tecnicas:
 *
 *  1) EXPRESIONES REGULARES: para reconocer palabras reservadas y operadores
 *     de forma declarativa (clase Pattern de Java).
 *
 *  2) AUTOMATA FINITO: para reconocer los numeros (delegado a AutomataFinito),
 *     demostrando el reconocimiento basado en estados.
 *
 * Las consultas no distinguen mayusculas/minusculas.
 */
public class AnalizadorLexico {

    private final AutomataFinito automata = new AutomataFinito();

    // Expresiones regulares de las categorias lexicas (palabras reservadas).
    private static final Pattern RE_COMANDO  = Pattern.compile("CONSULTAR|MONITOREAR|ALERTAR");
    private static final Pattern RE_METRICA  = Pattern.compile("HUMEDAD|TEMPERATURA|BATERIA");
    private static final Pattern RE_OPERADOR = Pattern.compile("<=|>=|<|>|=");

    /** Lista de errores lexicos hallados durante el escaneo. */
    private final List<String> errores = new ArrayList<>();

    public List<Token> analizar(String entrada) {
        errores.clear();
        List<Token> tokens = new ArrayList<>();
        if (entrada == null) entrada = "";
        String texto = entrada.trim();

        int i = 0;
        while (i < texto.length()) {
            char c = texto.charAt(i);

            // Ignorar espacios en blanco.
            if (Character.isWhitespace(c)) { i++; continue; }

            // Operadores relacionales (pueden ser de 1 o 2 caracteres).
            if (c == '<' || c == '>' || c == '=') {
                String dos = (i + 1 < texto.length()) ? texto.substring(i, i + 2) : "";
                if (RE_OPERADOR.matcher(dos).matches()) {
                    tokens.add(new Token(TipoToken.OPERADOR, dos, i));
                    i += 2;
                } else {
                    tokens.add(new Token(TipoToken.OPERADOR, String.valueOf(c), i));
                    i += 1;
                }
                continue;
            }

            // Numeros: se reconocen acumulando digitos/punto y validando con el AFD.
            if (Character.isDigit(c)) {
                int inicio = i;
                while (i < texto.length() &&
                       (Character.isDigit(texto.charAt(i)) || texto.charAt(i) == '.')) {
                    i++;
                }
                String num = texto.substring(inicio, i);
                if (automata.esNumero(num)) {
                    tokens.add(new Token(TipoToken.NUMERO, num, inicio));
                } else {
                    tokens.add(new Token(TipoToken.ERROR, num, inicio));
                    errores.add("Numero invalido '" + num + "' en posicion " + inicio);
                }
                continue;
            }

            // Palabras (reservadas): se acumulan letras y se clasifican por regex.
            if (Character.isLetter(c)) {
                int inicio = i;
                while (i < texto.length() && Character.isLetter(texto.charAt(i))) i++;
                String palabra = texto.substring(inicio, i).toUpperCase();
                tokens.add(clasificarPalabra(palabra, inicio));
                continue;
            }

            // Cualquier otro simbolo es un error lexico.
            errores.add("Simbolo no reconocido '" + c + "' en posicion " + i);
            tokens.add(new Token(TipoToken.ERROR, String.valueOf(c), i));
            i++;
        }

        tokens.add(new Token(TipoToken.EOF, "", texto.length()));
        return tokens;
    }

    /** Clasifica una palabra usando las expresiones regulares de cada categoria. */
    private Token clasificarPalabra(String palabra, int pos) {
        if (RE_COMANDO.matcher(palabra).matches())  return new Token(TipoToken.COMANDO, palabra, pos);
        if (RE_METRICA.matcher(palabra).matches())  return new Token(TipoToken.METRICA, palabra, pos);
        if (palabra.equals("DE"))      return new Token(TipoToken.DE, palabra, pos);
        if (palabra.equals("PARCELA")) return new Token(TipoToken.PARCELA, palabra, pos);
        if (palabra.equals("SENSOR"))  return new Token(TipoToken.SENSOR, palabra, pos);
        if (palabra.equals("DONDE"))   return new Token(TipoToken.DONDE, palabra, pos);
        if (palabra.equals("VALOR"))   return new Token(TipoToken.VALOR, palabra, pos);

        errores.add("Palabra reservada desconocida '" + palabra + "' en posicion " + pos);
        return new Token(TipoToken.ERROR, palabra, pos);
    }

    public List<String> getErrores() { return errores; }
    public boolean hayErrores() { return !errores.isEmpty(); }
}
