package com.paska.agritech.observer;

/**
 * PATRON OBSERVER - Interfaz Sujeto (Observable / Subject).
 * Permite suscribir, remover y notificar observadores de forma desacoplada.
 */
public interface Sujeto {
    void suscribir(Observador observador);
    void remover(Observador observador);
    void notificar(com.paska.agritech.model.LecturaSensor lectura, String mensaje);
}
