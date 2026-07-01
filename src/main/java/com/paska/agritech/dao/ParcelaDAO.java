package com.paska.agritech.dao;

import com.paska.agritech.model.Parcela;
import com.paska.agritech.singleton.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Parcela. Accede a PostgreSQL mediante la conexion unica del Singleton.
 */
public class ParcelaDAO {

    public List<Parcela> listar() {
        List<Parcela> lista = new ArrayList<>();
        String sql = "SELECT IdParcela, IdAgricultor, Ubicacion, Hectareas, estado "
                   + "FROM Parcela WHERE estado = 'Activo' ORDER BY IdParcela";
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return lista;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Parcela p = new Parcela();
                p.setIdParcela(rs.getInt("IdParcela"));
                p.setIdAgricultor(rs.getInt("IdAgricultor"));
                p.setUbicacion(rs.getString("Ubicacion"));
                p.setHectareas(rs.getDouble("Hectareas"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[ParcelaDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public int insertar(Parcela p) {
        String sql = "INSERT INTO Parcela (IdAgricultor, Ubicacion, Hectareas) VALUES (?, ?, ?)";
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return -1;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdAgricultor());
            ps.setString(2, p.getUbicacion());
            ps.setDouble(3, p.getHectareas());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ParcelaDAO] Error al insertar: " + e.getMessage());
        }
        return -1;
    }
}
