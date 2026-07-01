package com.paska.agritech.compiler.lexer;

/**
 * Categorias lexicas (tokens) reconocidas por el lenguaje de consultas
 * de Paska Agritech. Cada categoria es un simbolo terminal de la gramatica.
 */
public enum TipoToken {
    COMANDO,    // CONSULTAR, MONITOREAR, ALERTAR
    METRICA,    // HUMEDAD, TEMPERATURA, BATERIA
    DE,         // DE
    PARCELA,    // PARCELA
    SENSOR,     // SENSOR
    DONDE,      // DONDE
    VALOR,      // VALOR
    OPERADOR,   // < > <= >= =
    NUMERO,     // 0, 1, 2, ... 30.5
    EOF,        // fin de la entrada
    ERROR       // token no reconocido
}
