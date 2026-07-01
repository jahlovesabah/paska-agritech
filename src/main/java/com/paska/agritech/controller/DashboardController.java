package com.paska.agritech.controller;

import com.paska.agritech.compiler.lexer.AutomataFinito;
import com.paska.agritech.dao.AgricultorDAO;
import com.paska.agritech.model.Agricultor;
import com.paska.agritech.model.LecturaSensor;
import com.paska.agritech.observer.EstacionMonitoreo;
import com.paska.agritech.observer.ObservadorAppMovil;
import com.paska.agritech.observer.ObservadorCorreo;
import com.paska.agritech.service.ProcesadorConcurrente;
import com.paska.agritech.service.ProcesadorTelemetria;
import com.paska.agritech.service.ServicioConsulta;
import com.paska.agritech.singleton.ConexionBD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Pattern;

/**
 * CONTROLADOR (capa Controller del patron MVC  -  Objetivo Especifico 1).
 *
 * Recibe las peticiones HTTP, invoca la logica de negocio (servicios) y
 * selecciona la Vista (plantilla Thymeleaf) que se devuelve al usuario.
 * Mantiene la interfaz desacoplada de la logica (resuelve el alto acoplamiento
 * y la mezcla de logica con interfaz senalados en el Ishikawa).
 */
@Controller
public class DashboardController {

    @Autowired
    private ServicioConsulta servicioConsulta;

    private final ProcesadorTelemetria procesador = new ProcesadorTelemetria();
    private final AgricultorDAO agricultorDAO = new AgricultorDAO();
    private final AutomataFinito automata = new AutomataFinito();

    // Expresiones regulares para validar los campos del modelo de datos.
    private static final Pattern RE_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern RE_TELEFONO =
            Pattern.compile("^9\\d{8}$"); // celular peruano: 9 + 8 digitos

    // ---- VISTA: Panel principal ----
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("conectado", ConexionBD.getInstancia().estaConectada());
        return "index";
    }

    // ---- VISTA: Consola del lenguaje de consultas (compilador) ----
    @GetMapping("/consulta")
    public String consultaForm() {
        return "consulta";
    }

    @PostMapping("/consulta")
    public String consultaProcesar(@RequestParam("texto") String texto, Model model) {
        ServicioConsulta.ResultadoConsulta r = servicioConsulta.procesar(texto);
        model.addAttribute("entrada", texto);
        model.addAttribute("resultado", r);
        return "consulta";
    }

    // ---- VISTA: Registro de agricultor (validacion con regex + automata) ----
    @GetMapping("/agricultor")
    public String agricultorForm(Model model) {
        model.addAttribute("agricultores", agricultorDAO.listar());
        return "agricultor";
    }

    @PostMapping("/agricultor")
    public String agricultorGuardar(@RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String dni,
                                    @RequestParam String telefono,
                                    @RequestParam String correo,
                                    Model model) {
        StringBuilder errores = new StringBuilder();

        // Validacion con AUTOMATA FINITO: DNI = exactamente 8 digitos.
        if (!automata.esDNI(dni)) {
            errores.append("DNI invalido (debe tener exactamente 8 digitos). ");
        }
        // Validacion con EXPRESIONES REGULARES: correo y telefono.
        if (!RE_CORREO.matcher(correo).matches()) {
            errores.append("Correo con formato invalido. ");
        }
        if (!RE_TELEFONO.matcher(telefono).matches()) {
            errores.append("Telefono invalido (celular: 9 seguido de 8 digitos). ");
        }

        if (errores.length() > 0) {
            model.addAttribute("error", errores.toString());
        } else {
            Agricultor a = new Agricultor();
            a.setNombre(nombre);
            a.setApellido(apellido);
            a.setDni(dni);
            a.setTelefono(telefono);
            a.setCorreo(correo);
            int id = agricultorDAO.insertar(a);
            model.addAttribute("ok", id > 0
                    ? "Agricultor registrado correctamente (ID " + id + ")."
                    : "Validacion correcta. (Sin BD activa no se persistio el registro.)");
        }
        model.addAttribute("agricultores", agricultorDAO.listar());
        return "agricultor";
    }

    // ---- VISTA: Demostracion de concurrencia + programacion funcional ----
    @GetMapping("/concurrencia")
    public String concurrenciaForm() {
        return "concurrencia";
    }

    @PostMapping("/concurrencia")
    public String concurrenciaEjecutar(@RequestParam(defaultValue = "10000") int registros,
                                       @RequestParam(defaultValue = "5") int hilos,
                                       Model model) {
        // 1. Generar datos masivos.
        var lecturas = procesador.generarLecturas(registros);

        // 2. Configurar el patron Observer (dos observadores = dos alertas).
        EstacionMonitoreo estacion = new EstacionMonitoreo();
        estacion.suscribir(new ObservadorCorreo());
        estacion.suscribir(new ObservadorAppMovil());

        // 3. Procesar SECUENCIAL (1 hilo) para comparar.
        ProcesadorConcurrente secuencial = new ProcesadorConcurrente(estacion);
        var rSec = secuencial.procesar(lecturas, 1);

        // 4. Procesar CONCURRENTE (N hilos).
        ProcesadorConcurrente concurrente = new ProcesadorConcurrente(estacion);
        var rCon = concurrente.procesar(lecturas, hilos);

        // 5. Programacion funcional: estadisticas declarativas.
        double promHumedad = procesador.promedioHumedad(lecturas);
        long bateriaCritica = procesador.contarBateriaCritica(lecturas);

        double mejora = rSec.tiempoMs() == 0 ? 0
                : (1 - (double) rCon.tiempoMs() / rSec.tiempoMs()) * 100;

        model.addAttribute("registros", registros);
        model.addAttribute("hilos", hilos);
        model.addAttribute("tiempoSec", rSec.tiempoMs());
        model.addAttribute("tiempoCon", rCon.tiempoMs());
        model.addAttribute("mejora", String.format("%.1f", mejora));
        model.addAttribute("totalAlertas", rCon.totalAlertas());
        model.addAttribute("promHumedad", String.format("%.2f", promHumedad));
        model.addAttribute("bateriaCritica", bateriaCritica);
        model.addAttribute("observadores", estacion.getCantidadObservadores());
        return "concurrencia";
    }
}
