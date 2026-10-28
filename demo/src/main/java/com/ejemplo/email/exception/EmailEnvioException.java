package com.ejemplo.email.exception;

/**
 * Excepción lanzada cuando falla el envío del correo
 * al comunicarse con el Worker de Cloudflare.
 */
public class EmailEnvioException extends RuntimeException {

    /**
     * @param mensaje descripción del error
     */
    public EmailEnvioException(String mensaje) {
        super(mensaje);
    }

    /**
     * @param mensaje descripción del error
     * @param causa   excepción original
     */
    public EmailEnvioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
