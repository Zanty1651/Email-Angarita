package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Objeto de transferencia de datos (DTO) que representa la solicitud para enviar un correo.
 *
 * @param to      dirección de correo electrónico del destinatario (obligatorio y con formato válido).
 * @param subject asunto del correo (obligatorio, máximo 200 caracteres).
 * @param body    contenido HTML del correo (obligatorio).
 */
public record EmailRequest(
    @NotBlank(message = "es obligatorio")
    @Email(message = "debe ser un correo válido")
    String to,

    @NotBlank(message = "es obligatorio")
    @Size(max = 200, message = "no debe superar los 200 caracteres")
    String subject,

    @NotBlank(message = "es obligatorio")
    String body
) {}
