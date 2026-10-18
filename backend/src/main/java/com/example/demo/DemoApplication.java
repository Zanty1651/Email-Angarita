package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicializa la aplicación Spring Boot para el sistema de correos.
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args argumentos de línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
