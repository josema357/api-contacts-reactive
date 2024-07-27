package com.reactive.webflux.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "contacto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Contacto {
  private String id;
  private String nombre;
  private String email;
  private String telefono;

  public Contacto(String nombre, String email, String telefono) {
    this.nombre = nombre;
    this.email = email;
    this.telefono = telefono;
  }

  
}
