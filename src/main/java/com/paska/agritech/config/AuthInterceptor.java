package com.paska.agritech.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de autenticacion.
 * Bloquea el acceso a cualquier ruta protegida si no existe una sesion
 * iniciada, redirigiendo al formulario de login.
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            return true; // sesion valida: continua
        }
        // Sin sesion: redirige al login.
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
