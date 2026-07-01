package com.paska.agritech.service;

import com.paska.agritech.model.LecturaSensor;
import com.paska.agritech.observer.EstacionMonitoreo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * PROGRAMACION CONCURRENTE  -  Objetivo Especifico 5
 * "Ejecutar hilos concurrentes evaluando cinco parcelas simultaneamente para
 *  evitar bloqueos en la interfaz del sistema."
 *
 * Usa un pool de hilos (ExecutorService) para procesar las parcelas en
 * paralelo. Esto elimina el "Bloqueo del hilo principal (UI)" y el
 * "Procesamiento estrictamente mono-hilo" senalados en el Ishikawa.
 *
 * Cada hilo evalua una parcela y dispara alertas reactivas mediante la
 * EstacionMonitoreo (patron Observer), de forma totalmente desacoplada.
 */
public class ProcesadorConcurrente {

    private final EstacionMonitoreo estacion;

    public ProcesadorConcurrente(EstacionMonitoreo estacion) {
        this.estacion = estacion;
    }

    /** Resultado del procesamiento concurrente. */
    public record Resultado(long tiempoMs, int totalAlertas,
                            Map<Integer, Integer> alertasPorParcela) { }

    /**
     * Procesa concurrentemente las lecturas, agrupadas por parcela.
     * @param lecturas dataset masivo (p. ej. 10 000 registros).
     * @param numHilos numero de hilos del pool (p. ej. 5 = una por parcela).
     */
    public Resultado procesar(List<LecturaSensor> lecturas, int numHilos) {
        long inicio = System.currentTimeMillis();

        // Agrupar lecturas por parcela (una tarea concurrente por parcela).
        Map<Integer, List<LecturaSensor>> porParcela = lecturas.stream()
                .collect(Collectors.groupingBy(LecturaSensor::getIdParcela));

        ExecutorService pool = Executors.newFixedThreadPool(numHilos);
        AtomicInteger totalAlertas = new AtomicInteger(0);
        Map<Integer, Integer> alertasPorParcela = new ConcurrentHashMap<>();

        // Lanzar una tarea por cada parcela: se ejecutan en paralelo.
        porParcela.forEach((idParcela, datos) ->
            pool.submit(() -> {
                int alertasLocal = 0;
                for (LecturaSensor l : datos) {
                    alertasLocal += estacion.evaluar(l);  // dispara Observer
                }
                alertasPorParcela.put(idParcela, alertasLocal);
                totalAlertas.addAndGet(alertasLocal);
                System.out.printf("[Hilo %s] Parcela %d procesada (%d lecturas, %d alertas)%n",
                        Thread.currentThread().getName(), idParcela, datos.size(), alertasLocal);
            })
        );

        // Apagado ordenado: esperar a que todos los hilos terminen.
        pool.shutdown();
        try {
            pool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long tiempo = System.currentTimeMillis() - inicio;
        return new Resultado(tiempo, totalAlertas.get(), alertasPorParcela);
    }
}
