package com.ejemplo.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos que envía el cliente (Angular) para enviar un correo.
 *
 * @param to      correo del destinatario
 * @param subject asunto del correo
 * @param body    contenido del correo en HTML
 */
public record EmailRequest(
        @NotBlank @Email String to,
        @NotBlank String subject,
        @NotBlank String body) {
}