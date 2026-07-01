package com.paska.agritech.service;

import com.paska.agritech.compiler.lexer.AnalizadorLexico;
import com.paska.agritech.compiler.lexer.Token;
import com.paska.agritech.compiler.parser.AnalizadorSintactico;
import com.paska.agritech.compiler.parser.NodoConsulta;
import com.paska.agritech.compiler.semantic.ValidadorSemantico;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Fachada del procesamiento de lenguaje: orquesta las tres fases del
 * compilador del lenguaje de consultas de Paska Agritech.
 *
 *   1. Analisis lexico   (tokens, regex + automata finito)
 *   2. Analisis sintactico (gramatica libre de contexto)
 *   3. Validacion semantica (coherencia con el dominio)
 */
@Service
public class ServicioConsulta {

    /** Resultado consolidado del procesamiento de una consulta. */
    public static class ResultadoConsulta {
        public boolean exito;
        public List<String> tokens = new ArrayList<>();
        public List<String> errores = new ArrayList<>();
        public String consultaInterpretada;
        public String fase; // LEXICO | SINTACTICO | SEMANTICO | OK
    }

    public ResultadoConsulta procesar(String entrada) {
        ResultadoConsulta r = new ResultadoConsulta();

        // ---- FASE 1: ANALISIS LEXICO ----
        AnalizadorLexico lexico = new AnalizadorLexico();
        List<Token> tokens = lexico.analizar(entrada);
        tokens.forEach(t -> r.tokens.add(t.toString()));

        if (lexico.hayErrores()) {
            r.fase = "LEXICO";
            r.errores.addAll(lexico.getErrores());
            return r;
        }

        // ---- FASE 2: ANALISIS SINTACTICO ----
        AnalizadorSintactico sintactico = new AnalizadorSintactico(tokens);
        NodoConsulta nodo;
        try {
            nodo = sintactico.parsear();
        } catch (AnalizadorSintactico.ErrorSintactico e) {
            r.fase = "SINTACTICO";
            r.errores.add(e.getMessage());
            return r;
        }

        // ---- FASE 3: VALIDACION SEMANTICA ----
        ValidadorSemantico semantico = new ValidadorSemantico();
        if (!semantico.validar(nodo)) {
            r.fase = "SEMANTICO";
            r.errores.addAll(semantico.getErrores());
            return r;
        }

        // ---- CONSULTA VALIDA ----
        r.exito = true;
        r.fase = "OK";
        r.consultaInterpretada = nodo.toString();
        return r;
    }
}
