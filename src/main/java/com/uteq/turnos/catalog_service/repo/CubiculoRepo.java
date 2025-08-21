package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Cubiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CubiculoRepo extends JpaRepository<Cubiculo, Long> {
  boolean existsByCodigo(String codigo);
}