/**
 * Worker de Cloudflare que recibe peticiones de Spring Boot
 * y llama a la API de SendGrid para enviar correos.
 */

/** Variables de entorno (se configuran como secrets en Cloudflare) */
interface Env {
  SENDGRID_API_KEY: string;
  SENDGRID_FROM_EMAIL: string;
  WORKER_TOKEN: string;
}

/** Estructura del JSON que envía Spring Boot */
interface EmailPayload {
  to: string;
  subject: string;
  body: string;
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    // Solo aceptar POST
    if (request.method !== "POST") {
      return Response.json({ error: "Método no permitido" }, { status: 405 });
    }

    // Validar token de autenticación
    const token = request.headers.get("X-Auth-Token");
    if (token !== env.WORKER_TOKEN) {
      return Response.json({ error: "No autorizado" }, { status: 401 });
    }

    // Leer y validar el cuerpo
    let payload: EmailPayload;
    try {
      payload = await request.json();
    } catch {
      return Response.json({ error: "JSON inválido" }, { status: 400 });
    }

    if (!payload.to || !payload.subject || !payload.body) {
      return Response.json(
        { error: "Faltan campos: to, subject y body son obligatorios" },
        { status: 400 }
      );
    }

    // Armar el cuerpo para la API de SendGrid
    const sendgridBody = {
      personalizations: [{ to: [{ email: payload.to }] }],
      from: { email: env.SENDGRID_FROM_EMAIL },
      subject: payload.subject,
      content: [{ type: "text/html", value: payload.body }],
    };

    // Llamar a SendGrid
    const sgResponse = await fetch("https://api.sendgrid.com/v3/mail/send", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${env.SENDGRID_API_KEY}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(sendgridBody),
    });

    // SendGrid responde 202 cuando acepta el correo
    if (sgResponse.status === 202) {
      return Response.json({ mensaje: "Correo enviado" }, { status: 200 });
    }

    // Si SendGrid falla, devolver 502 con el código recibido
    return Response.json(
      { error: `SendGrid respondió con código ${sgResponse.status}` },
      { status: 502 }
    );
  },
};
