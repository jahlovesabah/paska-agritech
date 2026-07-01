package com.paska.agritech.compiler;

import com.paska.agritech.compiler.lexer.AutomataFinito;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias (JUnit 5) del Automata Finito Determinista.
 * Requerimiento 3.4 - pruebas unitarias.
 */
class AutomataFinitoTest {

    private final AutomataFinito automata = new AutomataFinito();

    @Test
    @DisplayName("Reconoce numeros enteros validos")
    void enterosValidos() {
        assertTrue(automata.esNumero("0"));
        assertTrue(automata.esNumero("30"));
        assertTrue(automata.esNumero("12345"));
    }

    @Test
    @DisplayName("Reconoce numeros decimales validos")
    void decimalesValidos() {
        assertTrue(automata.esNumero("30.5"));
        assertTrue(automata.esNumero("0.99"));
    }

    @Test
    @DisplayName("Rechaza numeros mal formados")
    void numerosInvalidos() {
        assertFalse(automata.esNumero("30."));   // termina en punto
        assertFalse(automata.esNumero(".5"));    // empieza en punto
        assertFalse(automata.esNumero("3a"));    // contiene letra
        assertFalse(automata.esNumero(""));      // vacio
        assertFalse(automata.esNumero(null));    // nulo
    }

    @Test
    @DisplayName("Valida DNI peruano de exactamente 8 digitos")
    void dniValido() {
        assertTrue(automata.esDNI("71234568"));
        assertTrue(automata.esDNI("00000001"));
    }

    @Test
    @DisplayName("Rechaza DNI con longitud o caracteres incorrectos")
    void dniInvalido() {
        assertFalse(automata.esDNI("712345"));    // 6 digitos
        assertFalse(automata.esDNI("712345678")); // 9 digitos
        assertFalse(automata.esDNI("7123456X"));  // contiene letra
        assertFalse(automata.esDNI(null));
    }
}
