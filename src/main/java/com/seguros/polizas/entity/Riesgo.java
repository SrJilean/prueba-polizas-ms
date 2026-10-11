package com.seguros.polizas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "riesgos")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Riesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore
    private Poliza poliza;

    @Column(nullable = false)
    private String direccion;

    @Column(name = "canon_individual", nullable = false, precision = 15, scale = 2)
    private BigDecimal canonIndividual;

    @Column(name = "prima_individual", nullable = false, precision = 15, scale = 2)
    private BigDecimal primaIndividual;

    @Column(name = "valor_asegurado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAsegurado;

    @Column(name = "estado_riesgo", nullable = false)
    private String estadoRiesgo; // ACTIVO, CANCELADO

    // Cambiado a relaciones ManyToOne para consistencia con la Póliza
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asegurado_id", nullable = false)
    private Actor asegurado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Actor beneficiario;
}