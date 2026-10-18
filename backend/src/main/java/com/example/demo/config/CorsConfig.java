package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuración de políticas de Cross-Origin Resource Sharing (CORS)
 * para permitir la comunicación segura entre el frontend en Angular y la API REST.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String allowedOrigin;

    /**
     * Constructor que inyecta la URL del frontend permitida en producción (si está configurada).
     *
     * @param allowedOrigin origen frontend adicional configurado desde variables de entorno.
     */
    public CorsConfig(@Value("${cors.allowed-origin:}") String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    /**
     * Define las reglas de mapeo CORS permitiendo únicamente los métodos POST y OPTIONS en las rutas /api/**.
     *
     * @param registry registro para configurar mapeos CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = new ArrayList<>();
        origins.add("http://localhost:4200");

        if (allowedOrigin != null && !allowedOrigin.isBlank()) {
            origins.add(allowedOrigin.trim());
        }

        registry.addMapping("/api/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("*");
    }
}
