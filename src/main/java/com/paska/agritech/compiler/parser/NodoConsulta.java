package com.paska.agritech.compiler.parser;

/**
 * Nodo del Arbol de Sintaxis Abstracta (AST) producido por el analizador
 * sintactico. Representa una consulta ya validada estructuralmente.
 */
public class NodoConsulta {
    private String comando;     // CONSULTAR | MONITOREAR | ALERTAR
    private String metrica;     // HUMEDAD | TEMPERATURA | BATERIA
    private String tipoFuente;  // PARCELA | SENSOR
    private int idFuente;       // identificador numerico de la fuente
    private boolean tieneFiltro;
    private String operador;    // < > <= >= =
    private double valorFiltro;

    public String getComando() { return comando; }
    public void setComando(String comando) { this.comando = comando; }
    public String getMetrica() { return metrica; }
    public void setMetrica(String metrica) { this.metrica = metrica; }
    public String getTipoFuente() { return tipoFuente; }
    public void setTipoFuente(String tipoFuente) { this.tipoFuente = tipoFuente; }
    public int getIdFuente() { return idFuente; }
    public void setIdFuente(int idFuente) { this.idFuente = idFuente; }
    public boolean isTieneFiltro() { return tieneFiltro; }
    public void setTieneFiltro(boolean tieneFiltro) { this.tieneFiltro = tieneFiltro; }
    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }
    public double getValorFiltro() { return valorFiltro; }
    public void setValorFiltro(double valorFiltro) { this.valorFiltro = valorFiltro; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(comando).append(" ").append(metrica)
          .append(" DE ").append(tipoFuente).append(" ").append(idFuente);
        if (tieneFiltro) {
            sb.append(" DONDE VALOR ").append(operador).append(" ").append(valorFiltro);
        }
        return sb.toString();
    }
}
