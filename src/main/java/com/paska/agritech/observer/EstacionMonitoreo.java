package com.paska.agritech.observer;

import com.paska.agritech.model.LecturaSensor;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON OBSERVER  -  Sujeto concreto (Objetivo Especifico 4).
 * "Programar el patron Observer despachando dos alertas automatizadas en
 *  tiempo real sobre anomalias climaticas agricolas."
 *
 * La estacion analiza cada lectura entrante y, al superar los umbrales
 * criticos, notifica de forma ASINCRONA a todos los observadores suscritos.
 * Esto resuelve la sub-causa "Consultas sincronas que requieren intervencion
 * manual" del Diagrama de Ishikawa.
 *
 * Umbrales de anomalia configurados:
 *   - Bateria  < 50%  -> alerta de bateria critica.
 *   - Humedad  < 30%  -> alerta de estres hidrico.
 *   - Temperatura > 35 C -> alerta de calor extremo.
 */
public class EstacionMonitoreo implements Sujeto {

    private final List<Observador> observadores = new ArrayList<>();

    private static final double UMBRAL_BATERIA = 50.0;
    private static final double UMBRAL_HUMEDAD = 30.0;
    private static final double UMBRAL_TEMPERATURA = 35.0;

    @Override
    public void suscribir(Observador observador) {
        observadores.add(observador);
    }

    @Override
    public void remover(Observador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar(LecturaSensor lectura, String mensaje) {
        // Despacho ASINCRONO: cada notificacion corre en su propio hilo,
        // evitando bloquear el hilo principal (reactividad en tiempo real).
        for (Observador obs : observadores) {
            new Thread(() -> obs.actualizar(lectura, mensaje)).start();
        }
    }

    /**
     * Evalua una lectura y dispara alertas reactivas si detecta anomalias.
     * @return cantidad de alertas generadas para esta lectura.
     */
    public int evaluar(LecturaSensor lectura) {
        int alertas = 0;

        if (lectura.getBateria() < UMBRAL_BATERIA) {
            notificar(lectura, String.format(
                "BATERIA CRITICA (%.1f%%) en parcela %d, sensor %d",
                lectura.getBateria(), lectura.getIdParcela(), lectura.getIdSensor()));
            alertas++;
        }
        if (lectura.getHumedad() < UMBRAL_HUMEDAD) {
            notificar(lectura, String.format(
                "ESTRES HIDRICO (humedad %.1f%%) en parcela %d",
                lectura.getHumedad(), lectura.getIdParcela()));
            alertas++;
        }
        if (lectura.getTemperatura() > UMBRAL_TEMPERATURA) {
            notificar(lectura, String.format(
                "CALOR EXTREMO (%.1f C) en parcela %d",
                lectura.getTemperatura(), lectura.getIdParcela()));
            alertas++;
        }
        return alertas;
    }

    public int getCantidadObservadores() { return observadores.size(); }
}
