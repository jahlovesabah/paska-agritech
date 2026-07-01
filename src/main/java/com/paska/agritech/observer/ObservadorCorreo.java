package com.paska.agritech.observer;

import com.paska.agritech.model.LecturaSensor;

/**
 * PATRON OBSERVER - Observador concreto N.1.
 * Simula el envio de una alerta por correo al personal de Paska.
 */
public class ObservadorCorreo implements Observador {
    @Override
    public void actualizar(LecturaSensor lectura, String mensaje) {
        System.out.println("[CORREO -> jsalinas@paska.com] " + mensaje);
    }
    @Override
    public String getNombre() { return "Notificacion por Correo"; }
}
