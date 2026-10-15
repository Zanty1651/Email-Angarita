package com.ejemplo.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de SendGrid leídas desde application.yml
 * (que a su vez toma los valores de variables de entorno).
 *
 * @param apiKey    clave de API de SendGrid
 * @param fromEmail correo remitente verificado en SendGrid
 * @param fromName  nombre que verá el destinatario
 */
@ConfigurationProperties(prefix = "sendgrid")
public record SendGridProperties(String apiKey, String fromEmail, String fromName) {
}