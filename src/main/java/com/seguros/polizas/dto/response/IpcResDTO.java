package com.seguros.polizas.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class IpcResDTO {
    private Long id;
    private Integer periodo;
    private BigDecimal valor;
}