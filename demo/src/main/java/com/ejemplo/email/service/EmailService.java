package com.ejemplo.email.service;

import com.ejemplo.email.config.SendGridProperties;
import com.ejemplo.email.dto.EmailRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de enviar correos transaccionales con SendGrid.
 */
@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final SendGridProperties properties;

    /**
     * Crea el servicio y el cliente de SendGrid con la API Key configurada.
     *
     * @param properties propiedades de SendGrid
     */
    public EmailService(SendGridProperties properties) {
        this.properties = properties;
        this.sendGrid = new SendGrid(properties.apiKey());
    }

    /**
     * Envía un correo en formato HTML.
     *
     * @param request datos del correo (destinatario, asunto y cuerpo)
     * @throws IOException si falla la comunicación con SendGrid
     * @throws IllegalStateException si SendGrid rechaza el envío
     */
    public void enviar(EmailRequest request) throws IOException {
        Email from = new Email(properties.fromEmail(), properties.fromName());
        Email to = new Email(request.to());
        Content content = new Content("text/html", request.body());
        Mail mail = new Mail(from, request.subject(), to, content);

        Request sgRequest = new Request();
        sgRequest.setMethod(Method.POST);
        sgRequest.setEndpoint("mail/send");
        sgRequest.setBody(mail.build());

        Response response = sendGrid.api(sgRequest);

        // SendGrid responde 202 cuando acepta el correo
        if (response.getStatusCode() != 202) {
            throw new IllegalStateException(
                    "SendGrid rechazó el envío. Código: " + response.getStatusCode());
        }
    }
}