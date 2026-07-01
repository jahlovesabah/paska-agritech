package com.paska.agritech.dao;

import com.paska.agritech.model.Agricultor;
import com.paska.agritech.singleton.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de Agricultor.
 * Accede a SQL Server SIEMPRE a traves de la conexion unica del Singleton
 * (ConexionBD), respetando el OE3. Mapea la tabla Agricultor del script maestro.
 */
public class AgricultorDAO {

    /** Lista todos los agricultores activos. */
    public List<Agricultor> listar() {
        List<Agricultor> lista = new ArrayList<>();
        String sql = "SELECT IdAgricultor, Nombre, Apellido, DNI, Telefono, Correo, estado "
                   + "FROM Agricultor WHERE estado = 'Activo'";

        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return lista; // sin BD: devuelve lista vacia

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Agricultor a = new Agricultor();
                a.setIdAgricultor(rs.getInt("IdAgricultor"));
                a.setNombre(rs.getString("Nombre"));
                a.setApellido(rs.getString("Apellido"));
                a.setDni(rs.getString("DNI"));
                a.setTelefono(rs.getString("Telefono"));
                a.setCorreo(rs.getString("Correo"));
                a.setEstado(rs.getString("estado"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[AgricultorDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    /** Inserta un nuevo agricultor y devuelve el id generado. */
    public int insertar(Agricultor a) {
        String sql = "INSERT INTO Agricultor (Nombre, Apellido, DNI, Telefono, Correo) "
                   + "VALUES (?, ?, ?, ?, ?)";

        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) return -1;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setString(3, a.getDni());
            ps.setString(4, a.getTelefono());
            ps.setString(5, a.getCorreo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AgricultorDAO] Error al insertar: " + e.getMessage());
        }
        return -1;
    }
}
