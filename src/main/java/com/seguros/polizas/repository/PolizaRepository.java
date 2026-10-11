package com.seguros.polizas.repository;

import com.seguros.polizas.entity.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findByTipoPolizaAndEstado(String tipoPoliza, String estado);
    List<Poliza> findByTipoPoliza(String tipoPoliza);
    List<Poliza> findByEstado(String estado);
}