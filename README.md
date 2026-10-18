# Sistema de Envío de Correos Electrónicos

Sistema full-stack para el envío desacoplado, seguro y escalable de correos electrónicos a través de una arquitectura serverless por capas.

---

## 1. ¿Qué hace el proyecto?

Permite a los usuarios enviar correos electrónicos en formato HTML desde una interfaz web moderna en **Angular**, procesando y validando la solicitud en un backend en **Spring Boot 3**, para luego delegar la entrega a una función serverless en **Cloudflare Workers**, la cual se autentica de forma segura con la API REST v3 de **SendGrid**.

### Reglas de Arquitectura y Seguridad
- **Angular** SOLO se comunica con **Spring Boot**. Nunca interactúa directamente con el Worker ni con SendGrid.
- **Spring Boot** SOLO se comunica con el **Cloudflare Worker**. No utiliza el SDK de SendGrid ni conoce su API Key.
- La autenticación entre Spring Boot y el Worker se realiza mediante un token compartido en la cabecera HTTP `X-Auth-Token`.
- **Solo el Cloudflare Worker** conoce y almacena la API Key de SendGrid como secreto seguro.

---

## 2. Diagrama de Flujo

```text
[ Usuario ]
    │ Completa formulario web
    ▼
[ Angular (Frontend) ]  (Puerto 4200)
    │
    │  POST /api/v1/send-email
    │  Cuerpo: { "to": "...", "subject": "...", "body": "..." }
    ▼
[ Spring Boot (Backend) ]  (Puerto 8080)
    │  1. Valida los datos (Bean Validation)
    │  2. Aplica timeouts de 10s
    │  POST {WORKER_URL}
    │  Header: X-Auth-Token: {WORKER_TOKEN}
    ▼
[ Cloudflare Worker (Serverless) ]  (Puerto 8787 local)
    │  1. Valida método POST y cabecera X-Auth-Token
    │  2. Construye payload para SendGrid
    │  POST https://api.sendgrid.com/v3/mail/send
    │  Header: Authorization: Bearer {SENDGRID_API_KEY}
    ▼
[ SendGrid API v3 ]
    │  Responde 202 Accepted
    │  Entrega del mensaje vía SMTP
    ▼
[ Bandeja de Entrada del Destinatario ]
```

---

## 3. Variables de Entorno

### Backend (Spring Boot / Railway)
| Variable | Descripción | Valor por defecto |
| :--- | :--- | :--- |
| `PORT` | Puerto HTTP donde escucha el servidor Spring Boot. | `8080` |
| `WORKER_URL` | URL donde está corriendo el Cloudflare Worker. | `http://localhost:8787` |
| `WORKER_TOKEN` | Token compartido para autenticarse ante el Worker. | *(Obligatorio, sin valor por defecto)* |
| `CORS_ALLOWED_ORIGIN` | URL del frontend en producción (ej. Vercel, Netlify o GitHub Pages). | *(Vacío en local)* |

### Worker Serverless (Cloudflare Workers)
| Secreto / Variable | Descripción | Modo de configuración |
| :--- | :--- | :--- |
| `SENDGRID_API_KEY` | Llave de API con permisos de envío en SendGrid. | `.dev.vars` (local) / `wrangler secret put` (prod) |
| `SENDGRID_FROM_EMAIL` | Correo remitente verificado en tu cuenta SendGrid (Single Sender). | `.dev.vars` (local) / `wrangler secret put` (prod) |
| `WORKER_TOKEN` | Token secreto que el Worker exige en la cabecera `X-Auth-Token`. | `.dev.vars` (local) / `wrangler secret put` (prod) |

---

## 4. Ejecución en Entorno Local (Windows PowerShell)

Para probar la integración completa en tu máquina, abre **tres terminales independientes** de PowerShell.

### Terminal 1: Cloudflare Worker (Puerto 8787)
1. Ingresa a la carpeta del Worker:
   ```powershell
   cd email-worker
   ```
2. Crea tu archivo de variables locales a partir de la plantilla:
   ```powershell
   Copy-Item .dev.vars.example .dev.vars
   ```
3. Edita `.dev.vars` con tu editor preferido y coloca tus credenciales reales de SendGrid y un token que elijas:
   ```ini
   SENDGRID_API_KEY=SG.tu_api_key_real
   SENDGRID_FROM_EMAIL=tu_correo_verificado@ejemplo.com
   WORKER_TOKEN=mi-token-secreto-compartido-123
   ```
4. Inicia el servidor de desarrollo del Worker:
   ```powershell
   npx.cmd wrangler dev
   ```
   *(El Worker quedará escuchando en `http://localhost:8787`)*.

---

### Terminal 2: Backend Spring Boot (Puerto 8080)
1. Ingresa a la carpeta del backend:
   ```powershell
   cd backend
   ```
2. Define la variable de entorno con el **mismo token** configurado en el Worker:
   ```powershell
   $env:WORKER_TOKEN="mi-token-secreto-compartido-123"
   ```
3. Inicia la aplicación con el Maven Wrapper:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   *(El backend quedará escuchando en `http://localhost:8080`)*.

---

### Terminal 3: Frontend Angular (Puerto 4200)
1. Ingresa a la carpeta del frontend:
   ```powershell
   cd frontend
   ```
2. Inicia el servidor de desarrollo de Angular:
   ```powershell
   npx.cmd ng serve
   ```
3. Abre tu navegador web en:
   ```text
   http://localhost:4200
   ```

---

## 5. Pruebas por Línea de Comandos (PowerShell)

### Health Check (Spring Boot)
Para comprobar que el backend está en línea:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -Method Get
```
*Respuesta esperada:*
```json
{
  "estado": "ok"
}
```

### Envío de Correo vía API REST
Para probar el flujo backend -> worker -> sendgrid sin abrir el navegador:
```powershell
$body = @{
    to = "tu_correo_personal@dominio.com"
    subject = "Prueba del sistema de correo"
    body = "<h1>¡Hola!</h1><p>Este es un correo enviado desde la arquitectura desacoplada.</p>"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/send-email" -Method Post -ContentType "application/json" -Body $body
```
*Respuesta esperada (HTTP 200):*
```json
{
  "mensaje": "Correo enviado correctamente"
}
```

---

## 6. Guía de Despliegue en Producción

### Paso A: Despliegue del Worker en Cloudflare

1. Desde la carpeta `email-worker`, inicia sesión en tu cuenta de Cloudflare:
   ```powershell
   cd email-worker
   npx.cmd wrangler login
   ```
2. Registra los 3 secretos en Cloudflare (te solicitará escribirlos en consola):
   ```powershell
   npx.cmd wrangler secret put SENDGRID_API_KEY
   npx.cmd wrangler secret put SENDGRID_FROM_EMAIL
   npx.cmd wrangler secret put WORKER_TOKEN
   ```
3. Despliega la función serverless:
   ```powershell
   npx.cmd wrangler deploy
   ```
4. Guarda la URL pública que te devolverá Wrangler (por ejemplo: `https://email-worker.<tu-subdominio>.workers.dev`).

---

### Paso B: Despliegue del Backend en Render

1. En [Render.com](https://dashboard.render.com/), haz clic en **New +** y selecciona **Web Service**.
2. Conecta tu cuenta de GitHub y selecciona el repositorio `Email-Angarita`.
3. Completa los campos de configuración:
   - **Name:** `email-backend` (o el nombre que elijas)
   - **Region:** La más cercana (ej. Oregon / Frankfurt)
   - **Branch:** `main`
   - **Root Directory:** `backend`
   - **Runtime:** `Docker` *(Render detectará automáticamente el Dockerfile en `backend/`)*
   - **Instance Type:** `Free`
4. En la sección **Environment Variables**, añade:
   - `WORKER_URL`: La URL pública de tu Cloudflare Worker desplegado (ej. `https://email-worker.<tu-subdominio>.workers.dev`).
   - `WORKER_TOKEN`: El token secreto compartido con el Worker.
   - `CORS_ALLOWED_ORIGIN`: La URL de tu frontend o déjalo vacío por ahora.
5. Haz clic en **Create Web Service**. Render compilará tu aplicación en la nube y te proporcionará una URL pública (ej. `https://email-backend-xxxx.onrender.com`).
6. Verifica el despliegue entrando a:
   `https://tu-servicio.onrender.com/api/v1/health`

---

### Paso C: Despliegue alternativo del Backend en Railway (opcional)

1. Sube tu proyecto a un repositorio de **GitHub**.
2. En [Railway.app](https://railway.app/), crea un nuevo proyecto seleccionando **Deploy from GitHub repo**.
3. Selecciona tu repositorio y ve a la pestaña **Settings**:
   - En **Root Directory**, escribe: `backend`
4. En la pestaña **Variables**, añade las siguientes variables de entorno:
   - `WORKER_URL`: La URL pública de tu Worker (ej. `https://email-worker.<tu-subdominio>.workers.dev`).
   - `WORKER_TOKEN`: El mismo token que guardaste en el secreto del Worker.
   - `CORS_ALLOWED_ORIGIN`: La URL donde alojarás tu frontend en Angular (si ya la tienes; si no, déjalo vacío inicialmente).
5. En la pestaña **Settings**, busca **Networking** y haz clic en **Generate Domain** para obtener tu URL pública.

---

### Paso D: Actualización del Frontend para Producción

1. En el archivo `frontend/src/environments/environment.ts`, reemplaza la constante `apiUrl` con la URL de Railway:
   ```typescript
   export const environment = {
     production: true,
     apiUrl: 'https://tu-dominio-railway.up.railway.app'
   };
   ```
2. Compila el frontend para producción:
   ```powershell
   cd frontend
   npx.cmd ng build
   ```
   *(Los archivos optimizados para publicar en cualquier hosting estático se generan en `frontend/dist/frontend/browser`)*.
