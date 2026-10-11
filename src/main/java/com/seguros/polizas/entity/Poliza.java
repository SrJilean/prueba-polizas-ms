package com.seguros.polizas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false, unique = true)
    private String numeroPoliza;

    @Column(name = "tipo_poliza", nullable = false)
    private String tipoPoliza; // COLECTIVA, INDIVIDUAL

    @Column(nullable = false)
    private String estado; // ACTIVA, CANCELADA, RENOVADA

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "vigencia_meses", nullable = false)
    private Integer vigenciaMeses;

    @Column(name = "valor_canon_total", precision = 15, scale = 2)
    private BigDecimal valorCanonTotal;

    @Column(name = "valor_prima_total", precision = 15, scale = 2)
    private BigDecimal valorPrimaTotal;

    @Column(name = "ipc_aplicado", precision = 5, scale = 2)
    private BigDecimal ipcAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tomador_id", nullable = false)
    private Actor tomador;

    @Column(name = "poliza_anterior_id")
    private Long polizaAnteriorId;

    // Se mapea la lista y el orphanRemoval para eliminar riesgos si se sacan de la lista
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos;
}