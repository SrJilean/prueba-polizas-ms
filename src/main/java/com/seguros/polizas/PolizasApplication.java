package com.seguros.polizas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PolizasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolizasApplication.class, args);
        System.out.println("🚀 Microservicio de Pólizas iniciado correctamente en el puerto 8080");
    }

}