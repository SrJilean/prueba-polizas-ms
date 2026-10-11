package com.seguros.polizas.repository;

import com.seguros.polizas.entity.HistoricoIPC;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HistoricoIPCRepository extends JpaRepository<HistoricoIPC, Long> {
    
    // Trae el registro más reciente para un periodo específico
    Optional<HistoricoIPC> findTopByPeriodoOrderByIdDesc(Integer periodo);
}