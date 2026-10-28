package com.ejemplo.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS.
 * Permite peticiones desde localhost:4200 (desarrollo)
 * y desde el origen configurado en la variable CORS_ALLOWED_ORIGIN (producción).
 */
@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGIN:}")
    private String corsAllowedOrigin;

    /**
     * @return configurador de CORS para los endpoints /api/**
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origenes;
                if (corsAllowedOrigin != null && !corsAllowedOrigin.isBlank()) {
                    origenes = new String[]{"http://localhost:4200", corsAllowedOrigin};
                } else {
                    origenes = new String[]{"http://localhost:4200"};
                }

                registry.addMapping("/api/**")
                        .allowedOrigins(origenes)
                        .allowedMethods("POST", "OPTIONS");
            }
        };
    }
}
