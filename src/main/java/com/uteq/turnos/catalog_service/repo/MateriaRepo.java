package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MateriaRepo extends JpaRepository<Materia, Long> {

  List<Materia> findByDivisionId(Long divisionId);

  // 👇 Carga la división junto con la materia para evitar lazy al serializar
  @Query("select m from Materia m join fetch m.division")
  List<Materia> findAllFetchDivision();

  // (si ya tenías existsByClave, déjalo)
  boolean existsByClave(String clave);
}
