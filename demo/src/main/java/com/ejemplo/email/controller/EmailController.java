package com.ejemplo.email.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejemplo.email.dto.EmailRequest;
import com.ejemplo.email.service.EmailService;

import jakarta.validation.Valid;

/**
 * Controlador REST para el envío de correos.
 */
@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    /**
     * @param emailService servicio de envío de correos
     */
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Recibe los datos del correo y los reenvía al Worker de Cloudflare.
     *
     * @param request datos del correo validados
     * @return respuesta 200 con un mensaje de confirmación
     */
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> enviar(@Valid @RequestBody EmailRequest request) {
        emailService.enviar(request);
        return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));
    }
}