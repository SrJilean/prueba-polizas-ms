package com.seguros.polizas.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RiesgoReqDTO {
    private String direccion;
    private BigDecimal canonIndividual;
    
    // Solo pedimos los IDs de los actores, no el objeto completo
    private Long aseguradoId; 
    private Long beneficiarioId;
}