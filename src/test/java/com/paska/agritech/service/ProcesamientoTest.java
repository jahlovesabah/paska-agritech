package com.paska.agritech.service;

import com.paska.agritech.model.LecturaSensor;
import com.paska.agritech.observer.EstacionMonitoreo;
import com.paska.agritech.observer.ObservadorAppMovil;
import com.paska.agritech.observer.ObservadorCorreo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias (JUnit 5) de la programacion funcional (OE2),
 * el patron Observer (OE4) y la concurrencia (OE5).
 * Requerimiento 3.4 - pruebas unitarias.
 */
class ProcesamientoTest {

    private final ProcesadorTelemetria procesador = new ProcesadorTelemetria();

    @Test
    @DisplayName("Genera la cantidad solicitada de lecturas")
    void generaLecturas() {
        List<LecturaSensor> datos = procesador.generarLecturas(10000);
        assertEquals(10000, datos.size());
    }

    @Test
    @DisplayName("El promedio de humedad cae dentro del rango simulado")
    void promedioHumedad() {
        List<LecturaSensor> datos = procesador.generarLecturas(5000);
        double prom = procesador.promedioHumedad(datos);
        assertTrue(prom >= 10 && prom <= 95);
    }

    @Test
    @DisplayName("El filtro funcional (lambda) devuelve un subconjunto coherente")
    void filtroFuncional() {
        List<LecturaSensor> datos = procesador.generarLecturas(1000);
        var calientes = procesador.filtrar(datos, l -> l.getTemperatura() > 40);
        assertTrue(calientes.size() <= datos.size());
        assertTrue(calientes.stream().allMatch(l -> l.getTemperatura() > 40));
    }

    @Test
    @DisplayName("Se suscriben dos observadores al patron Observer")
    void observadoresSuscritos() {
        EstacionMonitoreo est = new EstacionMonitoreo();
        est.suscribir(new ObservadorCorreo());
        est.suscribir(new ObservadorAppMovil());
        assertEquals(2, est.getCantidadObservadores());
    }

    @Test
    @DisplayName("El procesamiento secuencial y concurrente cuentan las mismas alertas")
    void concurrenciaConsistente() {
        List<LecturaSensor> datos = procesador.generarLecturas(3000);
        EstacionMonitoreo est = new EstacionMonitoreo();
        est.suscribir(new ObservadorCorreo());

        var rSec = new ProcesadorConcurrente(est).procesar(datos, 1);
        var rCon = new ProcesadorConcurrente(est).procesar(datos, 5);

        assertEquals(rSec.totalAlertas(), rCon.totalAlertas());
        assertTrue(rCon.totalAlertas() >= 0);
    }
}
