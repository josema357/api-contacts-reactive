package com.reactive.webflux.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.webflux.documents.Contacto;
import com.reactive.webflux.repository.ContactoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1")
public class ContactoController {
  private final ContactoRepository contactoRepository;

  public ContactoController(ContactoRepository contactoRepository) {
    this.contactoRepository = contactoRepository;
  }

  @GetMapping("/contactos")
  public Flux<Contacto> listarContactos(){
    return contactoRepository.findAll();
  }
  @GetMapping("/contactos/{id}")
  public Mono<ResponseEntity<Contacto>> obtenerContacto(@PathVariable String id){
    return contactoRepository.findById(id)
      .map( contacto -> new ResponseEntity<>(contacto, HttpStatus.OK))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  } 
  @GetMapping("/contactos/byEmail/{email}")
  public Mono<ResponseEntity<Contacto>> obtenerContactoPorEmail(@PathVariable String email){
    return contactoRepository.findFirstByEmail(email)
      .map( contacto -> new ResponseEntity<>(contacto, HttpStatus.OK))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
  @PostMapping("/contactos")
  public Mono<ResponseEntity<Contacto>> guardarContacto(@RequestBody Contacto contacto){
    return contactoRepository.insert(contacto)
      .map( contactoGuardado -> new ResponseEntity<>(contacto, HttpStatus.ACCEPTED))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE));
  }
  @PutMapping("/contactos/{id}")
  public Mono<ResponseEntity<Contacto>> actualizarContacto(@RequestBody Contacto contacto, @PathVariable String id){
    return contactoRepository.findById(id)
      .flatMap( contactoActualizado -> {
        contacto.setId(id);
        return contactoRepository.save(contacto)
          .map( contactoGuardado -> new ResponseEntity<>(contactoGuardado, HttpStatus.ACCEPTED));
      })
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  } 
  @DeleteMapping("/contactos/{id}")
  public Mono<Void> eliminarContacto(@PathVariable String id){
    return contactoRepository.deleteById(id);
  }
}
