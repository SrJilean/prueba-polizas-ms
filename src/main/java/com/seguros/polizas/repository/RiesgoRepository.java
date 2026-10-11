package com.seguros.polizas.repository;

import com.seguros.polizas.entity.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
}