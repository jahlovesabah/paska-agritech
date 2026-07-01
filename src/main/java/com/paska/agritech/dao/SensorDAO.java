package com.paska.agritech.dao;

import com.paska.agritech.model.Sensor;
import com.paska.agritech.singleton.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Sensor. Accede a PostgreSQL mediante la conexion unica del Singleton.
 */
public class SensorDAO {

    public List<Sensor> listar() {
        List<Sensor> lista = new ArrayList<>();
        String sql = "SELECT IdSensor, IdParcela, TipoSensor, Bateria, estado "
                   + "FROM Sensor WHERE estado = 'Activo' ORDER BY IdSensor";
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return lista;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sensor s = new Sensor();
                s.setIdSensor(rs.getInt("IdSensor"));
                s.setIdParcela(rs.getInt("IdParcela"));
                s.setTipoSensor(rs.getString("TipoSensor"));
                s.setBateria(rs.getDouble("Bateria"));
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[SensorDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /** Sensores con bateria por debajo de un umbral (consulta de monitoreo). */
    public List<Sensor> listarBateriaBaja(double umbral) {
        List<Sensor> lista = new ArrayList<>();
        String sql = "SELECT IdSensor, IdParcela, TipoSensor, Bateria, estado "
                   + "FROM Sensor WHERE Bateria < ? AND estado = 'Activo' ORDER BY Bateria";
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return lista;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, umbral);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sensor s = new Sensor();
                    s.setIdSensor(rs.getInt("IdSensor"));
                    s.setIdParcela(rs.getInt("IdParcela"));
                    s.setTipoSensor(rs.getString("TipoSensor"));
                    s.setBateria(rs.getDouble("Bateria"));
                    s.setEstado(rs.getString("estado"));
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SensorDAO] Error al filtrar: " + e.getMessage());
        }
        return lista;
    }

    public int insertar(Sensor s) {
        String sql = "INSERT INTO Sensor (IdParcela, TipoSensor, Bateria) VALUES (?, ?, ?)";
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return -1;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getIdParcela());
            ps.setString(2, s.getTipoSensor());
            ps.setDouble(3, s.getBateria());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[SensorDAO] Error al insertar: " + e.getMessage());
        }
        return -1;
    }
}
