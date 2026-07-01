package com.paska.agritech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del sistema Paska Agritech.
 *
 * Levanta el servidor web embebido (Tomcat) y todo el contexto de Spring (MVC).
 * Punto de entrada de la aplicacion concurrente para el monitoreo telemetrico
 * de parcelas agricolas del Peru.
 */
@SpringBootApplication
public class PaskaAgritechApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaskaAgritechApplication.class, args);
        System.out.println("""
                ============================================================
                  PASKA AGRITECH iniciado correctamente
                  Abre tu navegador en: http://localhost:8080
                ============================================================
                """);
    }
}
