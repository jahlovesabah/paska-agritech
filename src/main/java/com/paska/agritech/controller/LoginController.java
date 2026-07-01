package com.paska.agritech.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * CONTROLADOR de autenticacion (capa Controller del MVC).
 *
 * Implementa un inicio de sesion simple basado en sesion HTTP.
 * Credenciales validas: usuario = admin, contrasena = admin.
 */
@Controller
public class LoginController {

    private static final String USUARIO_VALIDO = "admin";
    private static final String CLAVE_VALIDA = "admin";

    @GetMapping("/login")
    public String formulario(HttpSession session) {
        // Si ya hay sesion activa, ir directo al panel.
        if (session.getAttribute("usuario") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String usuario,
                             @RequestParam String clave,
                             HttpSession session,
                             Model model) {
        if (USUARIO_VALIDO.equals(usuario) && CLAVE_VALIDA.equals(clave)) {
            session.setAttribute("usuario", usuario);
            return "redirect:/";
        }
        model.addAttribute("error", "Usuario o contrasena incorrectos.");
        return "login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
