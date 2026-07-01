package com.paska.agritech.observer;

import com.paska.agritech.model.LecturaSensor;

/**
 * PATRON OBSERVER  -  Objetivo Especifico 4
 * Interfaz Observador (Observer). Define el contrato de actualizacion que
 * reciben los suscriptores cuando el sujeto detecta una anomalia climatica.
 */
public interface Observador {
    void actualizar(LecturaSensor lectura, String mensaje);
    String getNombre();
}
