package com.paska.agritech.config;

import com.paska.agritech.singleton.ConexionBD;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Inicializador de la base de datos.
 *
 * Al arrancar la aplicacion crea automaticamente las tablas (schema.sql) y,
 * si la base esta vacia, inserta los datos de prueba (data.sql). Esto permite
 * que el despliegue en Neon funcione de inmediato sin pasos manuales.
 *
 * Usa SIEMPRE la conexion unica del Singleton (respeta el OE3) y es
 * idempotente (CREATE TABLE IF NOT EXISTS + carga condicional).
 */
@Component
public class InicializadorBD implements CommandLineRunner {

    @Override
    public void run(String... args) {
        Connection con = ConexionBD.getInstancia().getConexion();
        if (con == null) {
            System.out.println("[InicializadorBD] Sin conexion a BD: se omite la inicializacion (modo demostracion).");
            return;
        }
        try {
            ejecutarScript(con, "db/schema.sql");
            System.out.println("[InicializadorBD] Esquema verificado/creado correctamente.");

            if (estaVacia(con)) {
                ejecutarScript(con, "db/data.sql");
                System.out.println("[InicializadorBD] Datos de prueba insertados.");
            } else {
                System.out.println("[InicializadorBD] La base ya contiene datos: no se recargan.");
            }
        } catch (Exception e) {
            System.err.println("[InicializadorBD] Error al inicializar la BD: " + e.getMessage());
        }
    }

    /** Indica si la tabla Agricultor no tiene registros. */
    private boolean estaVacia(Connection con) {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Agricultor")) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (Exception e) {
            return false; // ante la duda, no recargar
        }
    }

    /** Lee un script del classpath y ejecuta cada sentencia (separadas por ';'). */
    private void ejecutarScript(Connection con, String ruta) throws Exception {
        ClassPathResource recurso = new ClassPathResource(ruta);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String t = linea.trim();
                if (t.isEmpty() || t.startsWith("--")) continue; // ignora comentarios
                sb.append(linea).append("\n");
            }
        }
        List<String> sentencias = new ArrayList<>();
        for (String s : sb.toString().split(";")) {
            if (!s.trim().isEmpty()) sentencias.add(s.trim());
        }
        try (Statement st = con.createStatement()) {
            for (String sentencia : sentencias) {
                st.execute(sentencia);
            }
        }
    }
}
