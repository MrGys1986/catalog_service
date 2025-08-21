package com.uteq.turnos.catalog_service.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> nf(NotFoundException e){ return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); }
  @ExceptionHandler(DuplicateException.class)
  public ResponseEntity<?> du(DuplicateException e){ return ResponseEntity.status(409).body(Map.of("error", e.getMessage())); }
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<?> cf(ConflictException e){ return ResponseEntity.status(409).body(Map.of("error", e.getMessage())); }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> iae(IllegalArgumentException e){ return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
}
