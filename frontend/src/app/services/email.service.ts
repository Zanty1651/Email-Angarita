import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EmailRequest, EmailResponse } from '../models/email-request';

/**
 * Servicio Angular para comunicarse con los endpoints REST del backend Spring Boot.
 */
@Injectable({
  providedIn: 'root'
})
export class EmailService {

  constructor(private http: HttpClient) {}

  /**
   * Envía los datos del formulario de correo al backend.
   *
   * @param datos objeto con destinatario, asunto y mensaje en formato HTML.
   * @returns un Observable con la respuesta enviada por la API REST.
   */
  enviar(datos: EmailRequest): Observable<EmailResponse> {
    return this.http.post<EmailResponse>(`${environment.apiUrl}/api/v1/send-email`, datos);
  }
}
