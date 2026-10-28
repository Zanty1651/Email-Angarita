package com.ejemplo.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ejemplo.email.dto.EmailRequest;
import com.ejemplo.email.exception.EmailEnvioException;

/**
 * Servicio que reenvía la petición de correo al Worker de Cloudflare.
 * Ya NO habla directamente con SendGrid.
 */
@Service
public class EmailService {

    private final RestClient restClient;
    private final String workerUrl;
    private final String workerToken;

    /**
     * @param workerUrl   URL del Worker de Cloudflare (variable WORKER_URL)
     * @param workerToken token compartido para autenticar con el Worker (variable WORKER_TOKEN)
     */
    public EmailService(
            @Value("${worker.url}") String workerUrl,
            @Value("${worker.token}") String workerToken) {
        this.workerUrl = workerUrl;
        this.workerToken = workerToken;
        this.restClient = RestClient.create();
    }

    /**
     * Envía los datos del correo al Worker de Cloudflare mediante POST.
     *
     * @param request datos del correo (destinatario, asunto y cuerpo)
     * @throws EmailEnvioException si el Worker responde con error o no responde
     */
    public void enviar(EmailRequest request) {
        try {
            restClient.post()
                    .uri(workerUrl)
                    .header("X-Auth-Token", workerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new EmailEnvioException(
                    "Error al comunicarse con el Worker: " + e.getMessage(), e);
        }
    }
}