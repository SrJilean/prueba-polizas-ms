package com.seguros.polizas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreIntegrationService {

    private final RestTemplate restTemplate;

    @Async
    public void notificarActualizacionCore(Long polizaId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("evento", "ACTUALIZACION");
            payload.put("polizaId", polizaId);
            restTemplate.postForEntity("http://localhost:8080/core-mock/evento", payload, Void.class);
        } catch (Exception e) {
            log.error("Error al notificar al Core Legado", e);
        }
    }
}