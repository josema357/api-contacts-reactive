package com.reactive.webflux.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.reactive.webflux.documents.Contacto;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class contactoControllerTest {
  @Autowired
  private WebTestClient webTestClient;

  private Contacto contactoGuardado;

  @Test
  @Order(0)
  public void testGuardarContacto(){
    Flux<Contacto> contactoFlux = webTestClient.post()
      .uri("/api/v1/contactos")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(new Contacto("Jose", "jose@gmail.com", "98987644")))
      .exchange()
      .expectStatus().isAccepted()
      .returnResult(Contacto.class).getResponseBody().log();

    contactoFlux.next().subscribe(contacto -> {
      this.contactoGuardado = contacto;
    });

    Assertions.assertNotNull(contactoGuardado);
  }
  @Test
  @Order(1)
  public void testObtenerContactoPorEmail(){
    Flux<Contacto> contactoFlux = webTestClient.get()
      .uri("/api/v1/contactos/byEmail/{email}", "jose@gmail.com")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .returnResult(Contacto.class).getResponseBody().log();
    StepVerifier.create(contactoFlux)
      .expectSubscription()
      .expectNextMatches(contacto -> contacto.getEmail().equals("jose@gmail.com"))
      .verifyComplete();
  }

  @Test
  @Order(2)
  public void actualizarContacto(){
    Flux<Contacto> contactoFlux = webTestClient.put()
      .uri("/api/v1/contactos/{id}", contactoGuardado.getId())
      .accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(new Contacto(contactoGuardado.getId(),"Jose Manuel", "jose@gmail.com", "111111111")))
      .exchange()
      .expectStatus().isAccepted()
      .returnResult(Contacto.class).getResponseBody().log();

    StepVerifier.create(contactoFlux)
      .expectSubscription()
      .expectNextMatches(contacto -> contacto.getEmail().equals("jose@gmail.com"))
      .verifyComplete();
  }

  @Test
  @Order(3)
  public void listarContactos(){
    Flux<Contacto> contactoFlux = webTestClient.get()
      .uri("/api/v1/contactos")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .returnResult(Contacto.class).getResponseBody().log();

    StepVerifier.create(contactoFlux)
      .expectSubscription()
      .expectNextCount(1)
      .verifyComplete();
  }

  @Test
  @Order(4)
  public void eliminarContactos(){
    Flux<Void> flux = webTestClient.delete()
      .uri("/api/v1/contactos/{id}", contactoGuardado.getId())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .returnResult(Void.class).getResponseBody().log();

    StepVerifier.create(flux)
      .expectSubscription()
      .verifyComplete();
  }
}
