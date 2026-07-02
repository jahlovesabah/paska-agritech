package com.paska.agritech.singleton;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PATRON SINGLETON  -  Objetivo Especifico 3
 * "Integrar el patron Singleton garantizando una conexion unica a la base de
 *  datos durante el ciclo operativo."
 *
 * Centraliza el recurso de conexion a PostgreSQL (Neon). Evita la creacion
 * redundante de instancias de conexion (sub-causa del Diagrama de Ishikawa),
 * previniendo el agotamiento rapido de memoria en el servidor.
 *
 * LECTURA DE CREDENCIALES (despliegue seguro en Render + Neon):
 *  - Opcion A (recomendada): variables separadas
 *      DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_SSL
 *  - Opcion B: variable unica DATABASE_URL, que acepta:
 *      a) Cadena nativa de Neon:  postgresql://usuario:clave@host/bd?sslmode=require
 *      b) Cadena JDBC:            jdbc:postgresql://host/bd?...   (+ DB_USER / DB_PASSWORD)
 *  - Si no hay ninguna variable, usa una BD PostgreSQL local por defecto.
 *
 * NUNCA se escriben credenciales en el codigo fuente: provienen del entorno.
 */
public final class ConexionBD {

    private static volatile ConexionBD instancia;
    private Connection conexion;

    // Valores efectivos de conexion (resueltos desde el entorno).
    private final String jdbcUrl;
    private final String usuario;
    private final String clave;

    /** Constructor privado: nadie fuera de esta clase puede instanciarla. */
    private ConexionBD() {
        String[] cfg = resolverConfiguracion();
        this.jdbcUrl = cfg[0];
        this.usuario = cfg[1];
        this.clave   = cfg[2];
        conectar();
    }

    /** Unico punto de acceso global (doble verificacion thread-safe). */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    private void conectar() {
        try {
            this.conexion = DriverManager.getConnection(jdbcUrl, usuario, clave);
            System.out.println("[Singleton] Conexion unica a PostgreSQL establecida (" + jdbcUrl + ")");
        } catch (SQLException e) {
            System.err.println("[Singleton] No se pudo conectar a la BD: " + e.getMessage());
            this.conexion = null;
        }
    }

    /** Devuelve la conexion centralizada; la reabre si se hubiera cerrado. */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            System.err.println("[Singleton] Error al obtener conexion: " + e.getMessage());
        }
        return conexion;
    }

    public boolean estaConectada() {
        try {
            return conexion != null && conexion.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Resuelve [jdbcUrl, usuario, clave] a partir del entorno.
     * Soporta el formato nativo de Neon (postgresql://) y el formato JDBC.
     */
    private String[] resolverConfiguracion() {
        String user = obtenerEnv("DB_USER");
        String pass = obtenerEnv("DB_PASSWORD");

        // Caso 1 (recomendado en Render): variables separadas DB_HOST, DB_PORT,
        // DB_NAME, DB_USER, DB_PASSWORD, DB_SSL.
        String host = obtenerEnv("DB_HOST");
        if (host != null && !host.isBlank()) {
            String port = obtenerEnv("DB_PORT");
            String name = obtenerEnv("DB_NAME");
            String ssl = obtenerEnv("DB_SSL");
            if (port == null || port.isBlank()) port = "5432";
            if (name == null || name.isBlank()) name = "neondb";
            boolean usarSsl = ssl == null || ssl.isBlank()
                    || ssl.equalsIgnoreCase("true") || ssl.equals("1")
                    || ssl.equalsIgnoreCase("require") || ssl.equalsIgnoreCase("yes");
            String jdbc = String.format("jdbc:postgresql://%s:%s/%s?sslmode=%s",
                    host, port, name, usarSsl ? "require" : "disable");
            return new String[]{
                    jdbc,
                    user != null ? user : "postgres",
                    pass != null ? pass : ""
            };
        }

        // Caso 2: variable unica DATABASE_URL (Neon nativa o JDBC).
        String env = obtenerEnv("DATABASE_URL");

        // Sin ninguna variable -> PostgreSQL local por defecto.
        if (env == null || env.isBlank()) {
            return new String[]{
                    "jdbc:postgresql://localhost:5432/PaskaAgritech_DB",
                    user != null ? user : "postgres",
                    pass != null ? pass : "postgres"
            };
        }

        // Formato nativo de Neon (postgresql:// o postgres://).
        if (env.startsWith("postgresql://") || env.startsWith("postgres://")) {
            try {
                URI uri = new URI(env);
                String userInfo = uri.getUserInfo();      // usuario:clave
                String uUser = user, uPass = pass;
                if (userInfo != null && userInfo.contains(":")) {
                    uUser = userInfo.split(":", 2)[0];
                    uPass = userInfo.split(":", 2)[1];
                }
                int puerto = uri.getPort() == -1 ? 5432 : uri.getPort();
                String query = construirQueryJdbc(uri.getQuery());
                String jdbc = String.format("jdbc:postgresql://%s:%d%s?%s",
                        uri.getHost(), puerto, uri.getPath(), query);
                return new String[]{jdbc, uUser, uPass};
            } catch (Exception e) {
                System.err.println("[Singleton] DATABASE_URL invalida: " + e.getMessage());
            }
        }

        // DATABASE_URL ya viene en formato JDBC.
        return new String[]{
                env,
                user != null ? user : "postgres",
                pass != null ? pass : ""
        };
    }

    /**
     * Construye la cadena de parametros para JDBC a partir de la query de Neon.
     * - Omite "channel_binding" (es parametro de libpq, no del driver pgJDBC).
     * - Garantiza la presencia de "sslmode=require" (Neon exige SSL).
     */
    private static String construirQueryJdbc(String original) {
        StringBuilder sb = new StringBuilder();
        boolean tieneSsl = false;
        if (original != null && !original.isBlank()) {
            for (String par : original.split("&")) {
                String clave = par.contains("=") ? par.substring(0, par.indexOf('=')) : par;
                if (clave.equalsIgnoreCase("channel_binding")) continue; // no soportado por pgJDBC
                if (clave.equalsIgnoreCase("sslmode")) tieneSsl = true;
                if (sb.length() > 0) sb.append("&");
                sb.append(par);
            }
        }
        if (!tieneSsl) {
            if (sb.length() > 0) sb.append("&");
            sb.append("sslmode=require");
        }
        return sb.toString();
    }

    /** Lee primero variables de entorno y luego propiedades del sistema (-D). */
    private static String obtenerEnv(String clave) {
        String v = System.getenv(clave);
        if (v == null || v.isBlank()) v = System.getProperty(clave);
        return v;
    }
}
