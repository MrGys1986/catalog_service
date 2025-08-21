package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="materia_docente",
  uniqueConstraints = { @UniqueConstraint(name="uq_md_materia", columnNames="materia_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MateriaDocente {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

  @OneToOne(optional=false) @JoinColumn(name="materia_id", nullable=false, unique=true)
  private Materia materia;

  @ManyToOne(optional=false) @JoinColumn(name="docente_id", nullable=false)
  private Docente docente;
}
