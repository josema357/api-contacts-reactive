package com.reactive.webflux.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import com.reactive.webflux.documents.Contacto;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactoRepositoryTest {
  @Autowired
  private ContactoRepository contactoRepository;
  @Autowired
  private ReactiveMongoOperations mongoOperations;

  @BeforeAll
  public void insertarDatos(){
    Contacto contacto1 = new Contacto("Jose", "jose@gmail.com", "98987644");
    Contacto contacto2 = new Contacto("andrea", "andrea@gmail.com", "98987644");
    Contacto contacto3 = new Contacto("diego", "diego@gmail.com", "98987644");

    StepVerifier.create(contactoRepository.insert(contacto1).log())
      .expectSubscription()
      .expectNextCount(1)
      .verifyComplete();
    StepVerifier.create(contactoRepository.save(contacto2).log())
      .expectSubscription()
      .expectNextCount(1)
      .verifyComplete();
    StepVerifier.create(contactoRepository.insert(contacto3).log())
      .expectSubscription()
      .expectNextMatches(contacto -> (contacto.getId() != null))
      .verifyComplete();
  }

  @Test
  @Order(1)
  public void testListarContactos(){
    StepVerifier.create(contactoRepository.findAll().log())
      .expectSubscription()
      .expectNextCount(3)
      .verifyComplete();
  }
  @Test
  @Order(2)
  public void testBuscarPorEmail(){
    StepVerifier.create(contactoRepository.findFirstByEmail("jose@gmail.com").log())
      .expectSubscription()
      .expectNextMatches(contacto -> contacto.getEmail().equals("jose@gmail.com"))
      .verifyComplete();
  }

  @Test
  @Order(3)
  public void actualizarContacto(){
    Mono<Contacto> contactoActualizado = contactoRepository.findFirstByEmail("jose@gmail.com")
      .map(contacto -> {
        contacto.setTelefono("111111111");
        return contacto;
      }).flatMap( contacto -> {
        return contactoRepository.save(contacto);
      });
    StepVerifier.create(contactoActualizado.log())
      .expectSubscription()
      .expectNextMatches( contacto -> contacto.getTelefono().equals("111111111"))
      .verifyComplete();
  }

  @Test
  @Order(4)
  public void eliminarContactoPorId(){
    Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("andrea@gmail.com")
      .flatMap(contacto -> {
        return contactoRepository.deleteById(contacto.getId());
      }).log();
    StepVerifier.create(contactoEliminado)
      .expectSubscription()
      .verifyComplete();
  }

  @Test
  @Order(5)
  public void eliminarContacto(){
    Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("diego@gmail.com")
      .flatMap(contacto -> contactoRepository.delete(contacto)).log();
    StepVerifier.create(contactoEliminado)
      .expectSubscription()
      .verifyComplete();
  }

  @AfterAll
  public void limpiarDatos(){
    Mono<Void> elementosEliminados = contactoRepository.deleteAll();
    StepVerifier.create(elementosEliminados.log())
      .expectSubscription()
      .verifyComplete();
  }
}
