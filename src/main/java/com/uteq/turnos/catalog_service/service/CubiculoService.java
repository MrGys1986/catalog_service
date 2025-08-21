package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Cubiculo;
import com.uteq.turnos.catalog_service.repo.CubiculoRepo;
import com.uteq.turnos.catalog_service.repo.DocenteCubiculoRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CubiculoService {
  private final CubiculoRepo repo;
  private final DocenteCubiculoRepo dcRepo;

  public CubiculoService(CubiculoRepo repo, DocenteCubiculoRepo dcRepo){
    this.repo = repo; this.dcRepo = dcRepo;
  }

  public Cubiculo create(Cubiculo c){
    if (repo.existsByCodigo(c.getCodigo())) throw new com.uteq.turnos.catalog_service.error.DuplicateException("Código de cubículo duplicado");
    return repo.save(c);
  }

  public Cubiculo update(Long id, Cubiculo in){
    Cubiculo c = repo.findById(id).orElseThrow(() -> new NotFoundException("Cubículo no encontrado"));
    if (!c.getCodigo().equals(in.getCodigo()) && repo.existsByCodigo(in.getCodigo()))
      throw new com.uteq.turnos.catalog_service.error.DuplicateException("Código duplicado");
    c.setCodigo(in.getCodigo()); c.setUbicacion(in.getUbicacion()); c.setActivo(in.getActivo());
    return repo.save(c);
  }

  public Cubiculo get(Long id){ return repo.findById(id).orElseThrow(() -> new NotFoundException("Cubículo no encontrado")); }
  public List<Cubiculo> list(){ return repo.findAll(); }

  @Transactional
  public void delete(Long id){
    if (!repo.existsById(id)) throw new NotFoundException("Cubículo no encontrado");
    // Limpia asignaciones para evitar violación de FK
    if (dcRepo != null) dcRepo.deleteByCubiculoId(id);
    repo.deleteById(id);
  }
}
