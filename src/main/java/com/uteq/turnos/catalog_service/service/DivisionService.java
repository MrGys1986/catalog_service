package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.error.DuplicateException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.repo.DivisionRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DivisionService {
  private final DivisionRepo repo;
  public DivisionService(DivisionRepo repo){ this.repo = repo; }

  public Division create(Division d){
    if (repo.existsByClave(d.getClave())) throw new DuplicateException("Clave ya registrada");
    return repo.save(d);
  }
  public Division update(Long id, Division in){
    Division d = repo.findById(id).orElseThrow(()->new NotFoundException("Division no encontrada"));
    if (!d.getClave().equals(in.getClave()) && repo.existsByClave(in.getClave()))
      throw new DuplicateException("Clave ya registrada");
    d.setNombre(in.getNombre()); d.setClave(in.getClave());
    return repo.save(d);
  }
  public void delete(Long id){ repo.deleteById(id); }
  public Division get(Long id){ return repo.findById(id).orElseThrow(()->new NotFoundException("Division no encontrada")); }
  public List<Division> list(){ return repo.findAll(); }
}
