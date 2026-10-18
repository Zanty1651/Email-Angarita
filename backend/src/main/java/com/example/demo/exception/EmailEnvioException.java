package com.example.demo.exception;

/**
 * Excepción personalizada que se lanza cuando ocurre un fallo al intentar enviar un correo
 * mediante el servicio externo (Cloudflare Worker o SendGrid).
 */
public class EmailEnvioException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje de error especificado.
     *
     * @param mensaje detalle explicativo del error ocurrido.
     */
    public EmailEnvioException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una nueva excepción con el mensaje de error y la causa original.
     *
     * @param mensaje detalle explicativo del error ocurrido.
     * @param causa   la causa original del fallo.
     */
    public EmailEnvioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
