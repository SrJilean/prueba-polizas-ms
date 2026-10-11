package com.seguros.polizas.controller;

import com.seguros.polizas.dto.request.RiesgoReqDTO;
import com.seguros.polizas.entity.Poliza;
import com.seguros.polizas.entity.Riesgo;
import com.seguros.polizas.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;

    @GetMapping
    public ResponseEntity<List<Poliza>> listarPolizas(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(polizaService.listarPolizas(tipo, estado));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<Riesgo>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarRiesgosPorPoliza(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovarPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.renovarPoliza(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelarPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelarPoliza(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Riesgo> agregarRiesgo(@PathVariable Long id, @RequestBody RiesgoReqDTO riesgoDTO) {
        return ResponseEntity.ok(polizaService.agregarRiesgoAPoliza(id, riesgoDTO));
    }
}