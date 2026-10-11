package com.seguros.polizas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "siniestros")
@Data
public class Siniestro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riesgo_id", nullable = false)
    private Riesgo riesgo;

    @Column(name = "fecha_siniestro", nullable = false)
    private LocalDate fechaSiniestro;

    @Column(name = "fecha_notificacion", nullable = false)
    private LocalDate fechaNotificacion;

    @Column(name = "estado_siniestro", nullable = false)
    private String estadoSiniestro; // REPORTADO, VALIDACION, APROBADO, PAGADO, RECHAZADO

    @Column(name = "monto_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "monto_pagado", precision = 15, scale = 2)
    private BigDecimal montoPagado;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}