package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepo extends JpaRepository<Division, Long> {
  boolean existsByClave(String clave);
  boolean existsByNombre(String nombre);
}
