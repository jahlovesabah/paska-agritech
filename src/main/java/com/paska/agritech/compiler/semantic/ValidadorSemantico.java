package com.paska.agritech.compiler.semantic;

import com.paska.agritech.compiler.parser.NodoConsulta;
import java.util.ArrayList;
import java.util.List;

/**
 * VALIDACION SEMANTICA BASICA  -  Tema del curso.
 *
 * Una consulta puede ser correcta sintacticamente pero carecer de sentido.
 * Esta capa verifica el SIGNIFICADO de la consulta sobre el dominio agricola:
 *
 *  - El identificador de parcela/sensor debe ser positivo y existir en el rango.
 *  - Los valores de filtro deben ser coherentes con la metrica:
 *        HUMEDAD y BATERIA -> rango 0 a 100 (porcentaje).
 *        TEMPERATURA       -> rango -10 a 60 grados Celsius.
 *  - El comando ALERTAR exige obligatoriamente un filtro (umbral).
 */
public class ValidadorSemantico {

    // Rango de identificadores validos segun los datos de la BD.
    private static final int MAX_PARCELAS = 100;
    private static final int MAX_SENSORES = 500;

    private final List<String> errores = new ArrayList<>();

    public boolean validar(NodoConsulta nodo) {
        errores.clear();

        // 1. Validar identificador de la fuente.
        if (nodo.getIdFuente() <= 0) {
            errores.add("El identificador de " + nodo.getTipoFuente()
                    + " debe ser un numero positivo.");
        } else if (nodo.getTipoFuente().equals("PARCELA") && nodo.getIdFuente() > MAX_PARCELAS) {
            errores.add("La parcela " + nodo.getIdFuente()
                    + " no existe (maximo permitido: " + MAX_PARCELAS + ").");
        } else if (nodo.getTipoFuente().equals("SENSOR") && nodo.getIdFuente() > MAX_SENSORES) {
            errores.add("El sensor " + nodo.getIdFuente()
                    + " no existe (maximo permitido: " + MAX_SENSORES + ").");
        }

        // 2. Validar coherencia del valor del filtro con la metrica.
        if (nodo.isTieneFiltro()) {
            double v = nodo.getValorFiltro();
            switch (nodo.getMetrica()) {
                case "HUMEDAD", "BATERIA" -> {
                    if (v < 0 || v > 100) {
                        errores.add(nodo.getMetrica()
                                + " debe estar entre 0 y 100 (porcentaje). Valor dado: " + v);
                    }
                }
                case "TEMPERATURA" -> {
                    if (v < -10 || v > 60) {
                        errores.add("TEMPERATURA debe estar entre -10 y 60 C. Valor dado: " + v);
                    }
                }
                default -> { /* sin restriccion adicional */ }
            }
        }

        // 3. Regla de negocio: ALERTAR requiere un umbral (filtro).
        if (nodo.getComando().equals("ALERTAR") && !nodo.isTieneFiltro()) {
            errores.add("El comando ALERTAR requiere una condicion 'DONDE VALOR <op> <numero>'.");
        }

        return errores.isEmpty();
    }

    public List<String> getErrores() { return errores; }
}
