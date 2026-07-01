package com.paska.agritech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion web (MVC). Registra el interceptor de autenticacion,
 * excluyendo el login y los recursos estaticos (CSS, imagenes, JS).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/error",
                        "/favicon.ico");
    }
}
