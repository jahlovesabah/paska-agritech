package com.paska.agritech.service;

import com.paska.agritech.model.LecturaSensor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * PROGRAMACION FUNCIONAL  -  Objetivo Especifico 2
 * "Aplicar programacion funcional procesando diez mil registros telemetricos
 *  para agilizar la transformacion de datos agricolas."
 *
 * Reemplaza los bucles for/while tradicionales por Streams, expresiones lambda
 * y funciones de orden superior. Esto resuelve la causa "Ineficiencia
 * algoritmica en la transformacion de datos" del Diagrama de Ishikawa.
 */
public class ProcesadorTelemetria {

    /**
     * Genera un dataset de prueba de N lecturas IoT (por defecto 10 000),
     * simulando los registros masivos provenientes de las parcelas.
     */
    public List<LecturaSensor> generarLecturas(int cantidad) {
        List<LecturaSensor> lecturas = new ArrayList<>(cantidad);
        String[] tipos = {"Humedad del Suelo", "Temperatura Ambiental"};
        for (int i = 0; i < cantidad; i++) {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            lecturas.add(new LecturaSensor(
                    r.nextInt(1, 6),                 // 5 parcelas
                    r.nextInt(1, 16),                // sensores
                    tipos[r.nextInt(tipos.length)],
                    r.nextDouble(10, 95),            // humedad %
                    r.nextDouble(12, 42),            // temperatura C
                    r.nextDouble(20, 100),           // bateria %
                    LocalDateTime.now().minusMinutes(i)
            ));
        }
        return lecturas;
    }

    /**
     * Filtra lecturas con una funcion de orden superior (recibe un Predicate).
     * Demuestra el paso de comportamiento como parametro (lambda).
     */
    public List<LecturaSensor> filtrar(List<LecturaSensor> datos, Predicate<LecturaSensor> criterio) {
        return datos.stream()
                    .filter(criterio)
                    .collect(Collectors.toList());
    }

    /**
     * Calcula el promedio de humedad usando map (transformacion) + average
     * (reduccion). Sin un solo bucle explicito.
     */
    public double promedioHumedad(List<LecturaSensor> datos) {
        return datos.stream()
                    .mapToDouble(LecturaSensor::getHumedad)
                    .average()
                    .orElse(0.0);
    }

    /**
     * Agrupa y resume las lecturas por parcela (groupingBy + summarizing),
     * generando estadisticas de temperatura por cada parcela.
     */
    public Map<Integer, DoubleSummaryStatistics> resumenTemperaturaPorParcela(List<LecturaSensor> datos) {
        return datos.stream()
                    .collect(Collectors.groupingBy(
                            LecturaSensor::getIdParcela,
                            Collectors.summarizingDouble(LecturaSensor::getTemperatura)));
    }

    /**
     * Cuenta cuantas lecturas presentan bateria critica (< 50%) de forma
     * declarativa.
     */
    public long contarBateriaCritica(List<LecturaSensor> datos) {
        return datos.stream()
                    .filter(l -> l.getBateria() < 50.0)
                    .count();
    }
}
