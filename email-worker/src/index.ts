/**
 * Definición de las variables de entorno y secretos inyectados por Cloudflare Workers.
 */
export interface Env {
  SENDGRID_API_KEY: string;
  SENDGRID_FROM_EMAIL: string;
  WORKER_TOKEN: string;
}

/**
 * Estructura esperada en el cuerpo de la petición enviada por Spring Boot.
 */
interface EmailPayload {
  to?: string;
  subject?: string;
  body?: string;
}

/**
 * Helper para generar respuestas HTTP en formato JSON de manera uniforme.
 *
 * @param data objeto a serializar como JSON.
 * @param status código de estado HTTP.
 */
function jsonResponse(data: unknown, status: number): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      "Content-Type": "application/json"
    }
  });
}

export default {
  /**
   * Manejador principal de peticiones entrantes para el Cloudflare Worker.
   *
   * @param request la petición HTTP recibida.
   * @param env los secretos y variables de entorno del Worker.
   * @param ctx contexto de ejecución del Worker.
   */
  async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
    // 1. Validar que el método HTTP sea POST
    if (request.method !== "POST") {
      return jsonResponse({ error: "Método no permitido" }, 405);
    }

    // 2. Validar autenticación compartida mediante el header X-Auth-Token
    const token = request.headers.get("X-Auth-Token");
    if (!token || token !== env.WORKER_TOKEN) {
      return jsonResponse({ error: "No autorizado" }, 401);
    }

    // 3. Parsear y validar el cuerpo JSON
    let payload: EmailPayload;
    try {
      payload = await request.json() as EmailPayload;
    } catch {
      return jsonResponse({ error: "Cuerpo JSON inválido" }, 400);
    }

    const { to, subject, body } = payload;
    if (!to || !subject || !body) {
      return jsonResponse({ error: "Faltan campos obligatorios: to, subject o body" }, 400);
    }

    // 4. Construir la petición hacia la API REST v3 de SendGrid
    const sendGridPayload = {
      personalizations: [
        {
          to: [{ email: to }]
        }
      ],
      from: {
        email: env.SENDGRID_FROM_EMAIL
      },
      subject: subject,
      content: [
        {
          type: "text/html",
          value: body
        }
      ]
    };

    try {
      const sendGridResponse = await fetch("https://api.sendgrid.com/v3/mail/send", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${env.SENDGRID_API_KEY}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(sendGridPayload)
      });

      // SendGrid retorna 202 Accepted cuando el correo ha sido encolado para entrega
      if (sendGridResponse.status === 202) {
        return jsonResponse({ mensaje: "Correo enviado" }, 200);
      }

      return jsonResponse(
        { error: `SendGrid respondió con código ${sendGridResponse.status}` },
        502
      );
    } catch (err) {
      return jsonResponse(
        { error: "Error de conexión al llamar a SendGrid" },
        502
      );
    }
  }
};
