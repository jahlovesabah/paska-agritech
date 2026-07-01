package com.paska.agritech.observer;

import com.paska.agritech.model.LecturaSensor;

/**
 * PATRON OBSERVER - Observador concreto N.2.
 * Simula una notificacion push hacia la App Movil del agricultor.
 */
public class ObservadorAppMovil implements Observador {
    @Override
    public void actualizar(LecturaSensor lectura, String mensaje) {
        System.out.println("[APP MOVIL - PUSH] " + mensaje);
    }
    @Override
    public String getNombre() { return "Notificacion App Movil"; }
}
