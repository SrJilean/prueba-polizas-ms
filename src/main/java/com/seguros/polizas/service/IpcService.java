package com.seguros.polizas.service;

import com.seguros.polizas.dto.request.IpcReqDTO;
import com.seguros.polizas.dto.response.IpcResDTO;
import com.seguros.polizas.entity.HistoricoIPC;
import com.seguros.polizas.exception.BusinessRuleException;
import com.seguros.polizas.repository.HistoricoIPCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpcService {

    private final HistoricoIPCRepository ipcRepository;

    @Transactional
    public IpcResDTO registrarIpc(IpcReqDTO dto) {
        // Regla: No permitir registrar el mismo periodo dos veces para mantener
        // limpieza
        // Si necesitas esto, agrega boolean existsByPeriodo(String periodo) en tu
        // HistoricoIPCRepository

        HistoricoIPC ipc = new HistoricoIPC();
        ipc.setPeriodo(dto.getPeriodo());
        ipc.setValor(dto.getValor());

        HistoricoIPC guardado = ipcRepository.save(ipc);
        return mapearADto(guardado);
    }

    @Transactional(readOnly = true)
    public List<IpcResDTO> obtenerTodos() {
        return ipcRepository.findAll().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private IpcResDTO mapearADto(HistoricoIPC ipc) {
        IpcResDTO dto = new IpcResDTO();
        dto.setId(ipc.getId());
        dto.setPeriodo(ipc.getPeriodo());
        dto.setValor(ipc.getValor());
        return dto;
    }

    // Añade este método a tu IpcService
    @Transactional(readOnly = true)
    public IpcResDTO obtenerUltimoIpcPorPeriodo(Integer periodo) {
        HistoricoIPC ultimoIpc = ipcRepository.findTopByPeriodoOrderByIdDesc(periodo)
                .orElseThrow(() -> new BusinessRuleException("No hay un IPC registrado para el periodo: " + periodo));

        return mapearADto(ultimoIpc);
    }
}