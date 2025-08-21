package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.MateriaDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MateriaDocenteRepo extends JpaRepository<MateriaDocente, Long> {
  Optional<MateriaDocente> findByMateriaId(Long materiaId);
  List<MateriaDocente> findByDocenteId(Long docenteId);
  boolean existsByMateriaId(Long materiaId);
}
