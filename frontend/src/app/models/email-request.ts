/**
 * Modelo de datos para la solicitud de envío de correo electrónico.
 */
export interface EmailRequest {
  to: string;
  subject: string;
  body: string;
}

/**
 * Modelo para la respuesta exitosa del servidor.
 */
export interface EmailResponse {
  mensaje: string;
}
