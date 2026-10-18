package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones para centralizar las respuestas de error de la API REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura los errores de validación de los datos de entrada (Spring Validation) y retorna un estado 400 Bad Request.
     *
     * @param ex excepción que contiene los errores de validación de los campos.
     * @return respuesta HTTP con código 400 y el primer error detectado en el cuerpo JSON.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        FieldError primerError = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        String mensajeError = (primerError != null)
                ? primerError.getField() + ": " + primerError.getDefaultMessage()
                : "Error de validación en la solicitud";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", mensajeError));
    }

    /**
     * Captura los fallos ocurridos durante el proceso de envío del correo y retorna un estado 502 Bad Gateway.
     *
     * @param ex excepción personalizada con el detalle del fallo.
     * @return respuesta HTTP con código 502 y el mensaje explicativo.
     */
    @ExceptionHandler(EmailEnvioException.class)
    public ResponseEntity<Map<String, String>> manejarFalloEnvio(EmailEnvioException ex) {
        String mensaje = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "No se pudo enviar el correo";

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", mensaje));
    }
}
