package com.ejemplo.email.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para respuestas consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (campos vacíos, email inválido, etc.).
     *
     * @param ex excepción de validación
     * @return respuesta 400 con el primer mensaje de error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");

        return ResponseEntity.badRequest().body(Map.of("error", mensaje));
    }

    /**
     * Maneja errores al comunicarse con el Worker de Cloudflare.
     *
     * @param ex excepción de envío
     * @return respuesta 502 con el mensaje del error
     */
    @ExceptionHandler(EmailEnvioException.class)
    public ResponseEntity<Map<String, String>> manejarEnvio(EmailEnvioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", ex.getMessage()));
    }
}
