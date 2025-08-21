package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.service.DivisionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/divisiones")
public class DivisionController {
  private final DivisionService svc;
  public DivisionController(DivisionService s){ this.svc=s; }

  @GetMapping public List<Division> list(){ return svc.list(); }
  @GetMapping("/{id}") public Division get(@PathVariable Long id){ return svc.get(id); }
  @PostMapping @PreAuthorize("hasRole('ADMIN')") public Division create(@RequestBody Division d){ return svc.create(d); }
  @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Division update(@PathVariable Long id, @RequestBody Division d){ return svc.update(id,d); }
  @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){ svc.delete(id); }
}
