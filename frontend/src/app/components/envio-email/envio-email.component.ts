import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EmailService } from '../../services/email.service';
import { EmailRequest } from '../../models/email-request';

/**
 * Componente standalone para el formulario de envío de correos electrónicos.
 */
@Component({
  selector: 'app-envio-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './envio-email.component.html',
  styleUrl: './envio-email.component.css'
})
export class EnvioEmailComponent {

  formulario: FormGroup;
  enviando = false;
  mensajeExito: string | null = null;
  mensajeError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private emailService: EmailService
  ) {
    this.formulario = this.fb.group({
      to: ['', [Validators.required, Validators.email]],
      subject: ['', [Validators.required, Validators.maxLength(200)]],
      body: ['', [Validators.required]]
    });
  }

  /**
   * Atajo para obtener el control del campo destinatario.
   */
  get toControl() {
    return this.formulario.get('to');
  }

  /**
   * Atajo para obtener el control del campo asunto.
   */
  get subjectControl() {
    return this.formulario.get('subject');
  }

  /**
   * Atajo para obtener el control del campo cuerpo del mensaje.
   */
  get bodyControl() {
    return this.formulario.get('body');
  }

  /**
   * Procesa el envío del correo electrónico al enviar el formulario.
   */
  enviarCorreo(): void {
    if (this.formulario.invalid || this.enviando) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando = true;
    this.mensajeExito = null;
    this.mensajeError = null;

    const datos: EmailRequest = this.formulario.value;

    this.emailService.enviar(datos).subscribe({
      next: () => {
        this.enviando = false;
        this.mensajeExito = 'Correo enviado correctamente';
        this.formulario.reset();
      },
      error: (error: HttpErrorResponse) => {
        this.enviando = false;
        if (error.status === 400) {
          this.mensajeError = 'Revisa los datos del formulario';
        } else if (error.status === 502) {
          this.mensajeError = 'No se pudo enviar el correo, intenta de nuevo';
        } else if (error.status === 0) {
          this.mensajeError = 'No hay conexión con el servidor';
        } else {
          this.mensajeError = 'Ocurrió un error inesperado';
        }
      }
    });
  }
}
