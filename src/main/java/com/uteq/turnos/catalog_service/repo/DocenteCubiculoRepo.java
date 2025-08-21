package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.DocenteCubiculo;
import com.uteq.turnos.catalog_service.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocenteCubiculoRepo extends JpaRepository<DocenteCubiculo, Long> {

  Optional<DocenteCubiculo> findByDocenteId(Long docenteId);

  Optional<DocenteCubiculo> findByCubiculoId(Long cubiculoId);

  void deleteByCubiculoId(Long cubiculoId);

  @Query("select dc.docente from DocenteCubiculo dc where dc.cubiculo.id = :cubiculoId")
  List<Docente> findDocentesByCubiculoId(Long cubiculoId);
}
