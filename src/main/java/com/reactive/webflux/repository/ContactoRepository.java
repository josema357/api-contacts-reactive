package com.reactive.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactive.webflux.documents.Contacto;

import reactor.core.publisher.Mono;

public interface ContactoRepository extends ReactiveMongoRepository<Contacto, String>{
  Mono<Contacto> findFirstByEmail(String email);
  Mono<Contacto> findAllByTelefonoOrNombre(String telfOrNombre);
}
