package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface DocenteRepo extends JpaRepository<Docente, Long> {
  Optional<Docente> findByUserId(Long userId);
  boolean existsByUserId(Long userId);
  List<Docente> findByDivision_Id(Long divisionId);
}
