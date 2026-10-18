import { Component } from '@angular/core';
import { EnvioEmailComponent } from './components/envio-email/envio-email.component';

/**
 * Componente principal de la aplicación Angular.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [EnvioEmailComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
