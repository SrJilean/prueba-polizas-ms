package com.seguros.polizas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "historico_ipc")
@Data
public class HistoricoIPC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer periodo; // Ej: 2024

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal valor;
}