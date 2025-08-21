package com.uteq.turnos.catalog_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "docente_cubiculo",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_dc_docente",  columnNames = "docente_id"),
    @UniqueConstraint(name = "uk_dc_cubiculo", columnNames = "cubiculo_id")
  }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class DocenteCubiculo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "docente_id", nullable = false, foreignKey = @ForeignKey(name="fk_dc_docente"))
  private Docente docente;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cubiculo_id", nullable = false, foreignKey = @ForeignKey(name="fk_dc_cubiculo"))
  private Cubiculo cubiculo;
}
