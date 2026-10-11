package com.seguros.polizas.repository;

import com.seguros.polizas.entity.HistoricoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoMovimientoRepository extends JpaRepository<HistoricoMovimiento, Long> {
}