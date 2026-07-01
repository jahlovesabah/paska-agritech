package com.paska.agritech.model;

/**
 * MODELO - Tabla Sensor (dispositivo IoT instalado en una Parcela).
 */
public class Sensor {
    private int idSensor;
    private int idParcela;
    private String tipoSensor;
    private double bateria;
    private String estado = "Activo";

    public Sensor() { }

    public Sensor(int idSensor, int idParcela, String tipoSensor, double bateria) {
        this.idSensor = idSensor;
        this.idParcela = idParcela;
        this.tipoSensor = tipoSensor;
        this.bateria = bateria;
    }

    public int getIdSensor() { return idSensor; }
    public void setIdSensor(int idSensor) { this.idSensor = idSensor; }
    public int getIdParcela() { return idParcela; }
    public void setIdParcela(int idParcela) { this.idParcela = idParcela; }
    public String getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(String tipoSensor) { this.tipoSensor = tipoSensor; }
    public double getBateria() { return bateria; }
    public void setBateria(double bateria) { this.bateria = bateria; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
