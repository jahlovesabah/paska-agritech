package com.paska.agritech.compiler;

import com.paska.agritech.service.ServicioConsulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias (JUnit 5) del compilador del lenguaje PASKA-QL:
 * analisis lexico, sintactico y validacion semantica.
 * Requerimiento 3.4 - pruebas unitarias.
 */
class ServicioConsultaTest {

    private final ServicioConsulta servicio = new ServicioConsulta();

    @Test
    @DisplayName("Consulta valida pasa las tres fases")
    void consultaValida() {
        var r = servicio.procesar("CONSULTAR HUMEDAD DE PARCELA 1 DONDE VALOR < 30");
        assertTrue(r.exito);
        assertEquals("OK", r.fase);
    }

    @Test
    @DisplayName("Consulta valida sin filtro tambien es aceptada")
    void consultaSinFiltro() {
        var r = servicio.procesar("MONITOREAR TEMPERATURA DE SENSOR 3");
        assertTrue(r.exito);
    }

    @Test
    @DisplayName("Token invalido produce error lexico")
    void errorLexico() {
        var r = servicio.procesar("CONSULTAR HUMEDAD DE PARCELA 3a");
        assertFalse(r.exito);
        assertEquals("LEXICO", r.fase);
    }

    @Test
    @DisplayName("Falta de palabra clave produce error sintactico")
    void errorSintactico() {
        var r = servicio.procesar("CONSULTAR HUMEDAD PARCELA 1"); // falta 'DE'
        assertFalse(r.exito);
        assertEquals("SINTACTICO", r.fase);
    }

    @Test
    @DisplayName("ALERTAR sin condicion produce error semantico")
    void errorSemanticoAlertar() {
        var r = servicio.procesar("ALERTAR HUMEDAD DE PARCELA 1");
        assertFalse(r.exito);
        assertEquals("SEMANTICO", r.fase);
    }

    @Test
    @DisplayName("Valor fuera de rango produce error semantico")
    void errorSemanticoRango() {
        var r = servicio.procesar("CONSULTAR HUMEDAD DE PARCELA 1 DONDE VALOR < 150");
        assertFalse(r.exito);
        assertEquals("SEMANTICO", r.fase);
    }

    @Test
    @DisplayName("Parcela inexistente produce error semantico")
    void errorSemanticoParcela() {
        var r = servicio.procesar("CONSULTAR HUMEDAD DE PARCELA 999");
        assertFalse(r.exito);
        assertEquals("SEMANTICO", r.fase);
    }
}
