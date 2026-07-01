package com.paska.agritech.model;

/**
 * MODELO - Tabla Parcela. Pertenece a un Agricultor.
 */
public class Parcela {
    private int idParcela;
    private int idAgricultor;
    private String ubicacion;
    private double hectareas;
    private String estado = "Activo";

    public Parcela() { }

    public Parcela(int idParcela, int idAgricultor, String ubicacion, double hectareas) {
        this.idParcela = idParcela;
        this.idAgricultor = idAgricultor;
        this.ubicacion = ubicacion;
        this.hectareas = hectareas;
    }

    public int getIdParcela() { return idParcela; }
    public void setIdParcela(int idParcela) { this.idParcela = idParcela; }
    public int getIdAgricultor() { return idAgricultor; }
    public void setIdAgricultor(int idAgricultor) { this.idAgricultor = idAgricultor; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public double getHectareas() { return hectareas; }
    public void setHectareas(double hectareas) { this.hectareas = hectareas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
