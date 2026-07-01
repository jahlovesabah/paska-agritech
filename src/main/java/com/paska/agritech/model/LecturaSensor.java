package com.paska.agritech.model;

import java.time.LocalDateTime;

/**
 * MODELO - Lectura telemetrica emitida por un sensor IoT.
 * Es la unidad de dato masivo que se procesa de forma funcional y concurrente
 * (corresponde a la tabla de hechos Hecho_Lectura_Sensores del modelo OLAP).
 */
public class LecturaSensor {
    private int idParcela;
    private int idSensor;
    private String tipoSensor;
    private double humedad;
    private double temperatura;
    private double bateria;
    private LocalDateTime fecha;

    public LecturaSensor() { }

    public LecturaSensor(int idParcela, int idSensor, String tipoSensor,
                         double humedad, double temperatura, double bateria,
                         LocalDateTime fecha) {
        this.idParcela = idParcela;
        this.idSensor = idSensor;
        this.tipoSensor = tipoSensor;
        this.humedad = humedad;
        this.temperatura = temperatura;
        this.bateria = bateria;
        this.fecha = fecha;
    }

    public int getIdParcela() { return idParcela; }
    public void setIdParcela(int idParcela) { this.idParcela = idParcela; }
    public int getIdSensor() { return idSensor; }
    public void setIdSensor(int idSensor) { this.idSensor = idSensor; }
    public String getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(String tipoSensor) { this.tipoSensor = tipoSensor; }
    public double getHumedad() { return humedad; }
    public void setHumedad(double humedad) { this.humedad = humedad; }
    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }
    public double getBateria() { return bateria; }
    public void setBateria(double bateria) { this.bateria = bateria; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
