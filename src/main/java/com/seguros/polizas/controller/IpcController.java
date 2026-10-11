package com.seguros.polizas.controller;

import com.seguros.polizas.dto.request.IpcReqDTO;
import com.seguros.polizas.dto.response.IpcResDTO;
import com.seguros.polizas.service.IpcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ipc")
@RequiredArgsConstructor
@Tag(name = "Gestión de IPC", description = "Endpoints para el histórico de IPC (Inmutable)")
public class IpcController {

    private final IpcService ipcService;

    @PostMapping
    @Operation(summary = "Registrar nuevo IPC", description = "Añade un valor de IPC para un periodo específico")
    public ResponseEntity<IpcResDTO> registrarIpc(@Valid @RequestBody IpcReqDTO dto) {
        return new ResponseEntity<>(ipcService.registrarIpc(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar IPC", description = "Obtiene todo el histórico de IPC")
    public ResponseEntity<List<IpcResDTO>> obtenerIpcs() {
        return ResponseEntity.ok(ipcService.obtenerTodos());
    }

    // Añade este endpoint a tu IpcController
    @GetMapping("/{periodo}/ultimo")
    @Operation(summary = "Obtener último IPC", description = "Obtiene el valor de IPC más reciente registrado para un periodo")
    public ResponseEntity<IpcResDTO> obtenerUltimoIpc(@PathVariable Integer periodo) {
        return ResponseEntity.ok(ipcService.obtenerUltimoIpcPorPeriodo(periodo));
    }
}