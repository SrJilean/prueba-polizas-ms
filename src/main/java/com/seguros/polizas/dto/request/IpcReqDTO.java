package com.seguros.polizas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class IpcReqDTO {
    @NotBlank(message = "El periodo es obligatorio (ej. 2026)")
    private Integer periodo;

    @NotNull(message = "El valor del IPC es obligatorio")
    private BigDecimal valor;
}