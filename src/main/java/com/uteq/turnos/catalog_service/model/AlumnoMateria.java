package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="alumno_materia",
  uniqueConstraints=@UniqueConstraint(name="uq_am_par", columnNames={"alumno_id","materia_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlumnoMateria {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "alumno_id")
private Alumno alumno;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "materia_id")
private Materia materia;
}
