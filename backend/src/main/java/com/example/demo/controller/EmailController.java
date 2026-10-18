package com.example.demo.controller;

import com.example.demo.dto.EmailRequest;
import com.example.demo.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST que expone los endpoints para el envío de correos y la comprobación de estado de la API.
 */
@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    /**
     * Constructor con inyección de dependencias del servicio de correo.
     *
     * @param emailService servicio de lógica de envío de correos.
     */
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Endpoint para procesar y enviar un correo electrónico.
     *
     * @param request datos del correo recibidos en formato JSON y validados mediante Bean Validation.
     * @return respuesta HTTP 200 con mensaje de confirmación de envío.
     */
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailRequest request) {
        emailService.enviar(request);
        return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));
    }

    /**
     * Endpoint de comprobación de salud (Health Check) útil para monitoreo y despliegue en plataformas como Railway.
     *
     * @return respuesta HTTP 200 con el estado del servicio en JSON.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("estado", "ok"));
    }
}
