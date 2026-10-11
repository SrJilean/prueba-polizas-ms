package com.seguros.polizas.controller;

import com.seguros.polizas.entity.Riesgo;
import com.seguros.polizas.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final PolizaService polizaService;

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Riesgo> cancelarRiesgo(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelarRiesgo(id));
    }
}