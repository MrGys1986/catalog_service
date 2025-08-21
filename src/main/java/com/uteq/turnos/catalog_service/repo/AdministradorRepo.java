package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepo extends JpaRepository<Administrador, Long> {
  Optional<Administrador> findByUserId(Long userId);
  boolean existsByUserId(Long userId);
}
