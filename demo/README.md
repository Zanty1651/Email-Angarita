# Email Service — Angarita

Servicio de envío de correos transaccionales.

## Flujo de la arquitectura

```
Angular (frontend)
    │
    │  POST /api/v1/send-email
    ▼
Spring Boot (Railway)
    │
    │  POST con X-Auth-Token
    ▼
Worker de Cloudflare
    │
    │  POST a SendGrid API
    ▼
SendGrid → Correo al usuario
```

## Variables de entorno

### Spring Boot (Railway)

| Variable               | Descripción                                       |
|------------------------|---------------------------------------------------|
| `PORT`                 | Puerto del servidor (Railway lo asigna automático) |
| `WORKER_URL`           | URL del Worker de Cloudflare                       |
| `WORKER_TOKEN`         | Token compartido para autenticar con el Worker     |
| `CORS_ALLOWED_ORIGIN`  | Origen permitido en producción (ej: URL del front) |

### Worker de Cloudflare (secrets)

| Variable              | Descripción                          |
|-----------------------|--------------------------------------|
| `SENDGRID_API_KEY`    | API Key de SendGrid                  |
| `SENDGRID_FROM_EMAIL` | Correo remitente verificado          |
| `WORKER_TOKEN`        | Token compartido (mismo que Railway) |

Los secrets del Worker se configuran con:
```bash
cd email-worker
npx wrangler secret put SENDGRID_API_KEY
npx wrangler secret put SENDGRID_FROM_EMAIL
npx wrangler secret put WORKER_TOKEN
```

## Ejemplo de petición

```json
POST /api/v1/send-email
Content-Type: application/json

{
  "to": "destinatario@ejemplo.com",
  "subject": "Asunto del correo",
  "body": "<h1>Hola</h1><p>Este es el contenido en HTML.</p>"
}
```

Respuesta exitosa:
```json
{
  "mensaje": "Correo enviado correctamente"
}
```

## Desarrollo local

### Spring Boot
```bash
cd demo
set WORKER_URL=http://localhost:8787
set WORKER_TOKEN=mi-token-local
.\mvnw.cmd spring-boot:run
```

### Worker
```bash
cd demo/email-worker
npm install
npx wrangler dev
```
