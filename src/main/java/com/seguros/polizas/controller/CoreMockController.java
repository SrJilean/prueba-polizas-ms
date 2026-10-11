package com.seguros.polizas.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/core-mock")
@Slf4j
public class CoreMockController {

    @PostMapping("/evento")
    public ResponseEntity<Void> recibirEvento(@RequestBody Map<String, Object> payload) {
        log.info("::: SIMULACIÓN CORE LEGADO ::: Evento recibido: {} para la póliza ID: {}", 
                 payload.get("evento"), payload.get("polizaId"));
        return ResponseEntity.ok().build();
    }
}