package com.paska.agritech.model;

/**
 * MODELO (capa Model del patron MVC).
 * Representa la tabla Agricultor del modelo relacional transaccional (OLTP).
 */
public class Agricultor {
    private int idAgricultor;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String estado = "Activo";

    public Agricultor() { }

    public Agricultor(int idAgricultor, String nombre, String apellido,
                      String dni, String telefono, String correo) {
        this.idAgricultor = idAgricultor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdAgricultor() { return idAgricultor; }
    public void setIdAgricultor(int idAgricultor) { this.idAgricultor = idAgricultor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCompleto() { return nombre + " " + apellido; }
}
