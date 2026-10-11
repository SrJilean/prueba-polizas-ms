package com.seguros.polizas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_movimientos")
@Data
public class HistoricoMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "valor_canon_anterior", precision = 15, scale = 2)
    private BigDecimal valorCanonAnterior;

    @Column(name = "valor_canon_nuevo", precision = 15, scale = 2)
    private BigDecimal valorCanonNuevo;

    @Column(name = "valor_prima_anterior", precision = 15, scale = 2)
    private BigDecimal valorPrimaAnterior;

    @Column(name = "valor_prima_nuevo", precision = 15, scale = 2)
    private BigDecimal valorPrimaNuevo;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento; // INCLUSION_RIESGO, EXCLUSION_RIESGO, RENOVACION_AUTOMATICA

    @Column(name = "ipc_aplicado", precision = 5, scale = 2)
    private BigDecimal ipcAplicado;
}