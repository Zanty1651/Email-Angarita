import { Container, getContainer } from "@cloudflare/containers";

// Variables y secretos que el Worker recibe de Cloudflare
interface Env {
  EMAIL_CONTAINER: DurableObjectNamespace<EmailContainer>;
  SENDGRID_API_KEY: string;
  SENDGRID_FROM_EMAIL: string;
  SENDGRID_FROM_NAME?: string;
}

/**
 * Contenedor que ejecuta la aplicación Spring Boot.
 */
export class EmailContainer extends Container<Env> {
  // Puerto en el que escucha Spring Boot dentro del contenedor
  defaultPort = 8080;

  // Tiempo de inactividad antes de apagar el contenedor
  sleepAfter = "10m";

  constructor(ctx: DurableObjectState<{}>, env: Env) {
    super(ctx, env);
    // Pasa los secretos de Cloudflare como variables de entorno a Spring Boot
    this.envVars = {
      SENDGRID_API_KEY: env.SENDGRID_API_KEY,
      SENDGRID_FROM_EMAIL: env.SENDGRID_FROM_EMAIL,
      SENDGRID_FROM_NAME: env.SENDGRID_FROM_NAME ?? "Mi Aplicación",
    };
  }
}

export default {
  /**
   * Recibe todas las peticiones y las reenvía al contenedor.
   * Usa una sola instancia, porque el servicio no guarda estado.
   */
  async fetch(request: Request, env: Env): Promise<Response> {
    return getContainer(env.EMAIL_CONTAINER).fetch(request);
  },
};