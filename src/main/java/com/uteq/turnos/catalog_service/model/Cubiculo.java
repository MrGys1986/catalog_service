package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cubiculos", uniqueConstraints=@UniqueConstraint(name="uq_cub_codigo", columnNames="codigo"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cubiculo {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true, length=32) private String codigo;
  @Column(length=128) private String ubicacion;
  @Column(nullable=false) private Boolean activo = true;
}
