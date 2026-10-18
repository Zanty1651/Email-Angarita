package com.example.demo.service;

import com.example.demo.dto.EmailRequest;
import com.example.demo.exception.EmailEnvioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Servicio encargado de la comunicación HTTP hacia el Cloudflare Worker para el envío de correos.
 */
@Service
public class EmailService {

    private final RestClient restClient;
    private final String workerToken;

    /**
     * Constructor que inicializa el cliente HTTP RestClient con las propiedades del Worker
     * y configura los tiempos de espera (timeouts) de conexión y lectura a 10 segundos.
     *
     * @param workerUrl   URL base del Cloudflare Worker inyectada desde la configuración.
     * @param workerToken Token secreto de autenticación compartido con el Worker.
     */
    public EmailService(
            @Value("${worker.url}") String workerUrl,
            @Value("${worker.token}") String workerToken) {
        this.workerToken = workerToken;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl(workerUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Envía la solicitud de correo al Cloudflare Worker mediante una petición HTTP POST.
     *
     * @param request datos del correo que serán reenviados al Worker.
     * @throws EmailEnvioException si la conexión falla o el Worker responde con un código de error.
     */
    public void enviar(EmailRequest request) {
        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Auth-Token", this.workerToken)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw new EmailEnvioException("No se pudo enviar el correo", ex);
        }
    }
}
