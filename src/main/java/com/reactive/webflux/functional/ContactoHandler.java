package com.reactive.webflux.functional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.webflux.documents.Contacto;
import com.reactive.webflux.repository.ContactoRepository;

import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;

@Component
public class ContactoHandler {
  private ContactoRepository contactoRepository;

  public ContactoHandler(ContactoRepository contactoRepository) {
    this.contactoRepository = contactoRepository;
  }
  
  private Mono<ServerResponse> response404 = ServerResponse.notFound().build();
  private Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

  public Mono<ServerResponse> listarContactos(ServerRequest request){
    return ServerResponse.ok()
      .contentType(MediaType.APPLICATION_JSON)
      .body(contactoRepository.findAll(), Contacto.class);
  }

  public Mono<ServerResponse> obtenerContactoPorId(ServerRequest request){
    String id = request.pathVariable("id");
    return contactoRepository.findById(id)
      .flatMap( contacto -> 
        ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(fromValue(contacto)))
      .switchIfEmpty(response404);
  }

  public Mono<ServerResponse> obtenerContactoPorEmail(ServerRequest request){
    String email = request.pathVariable("email");
    return contactoRepository.findFirstByEmail(email)
      .flatMap( contacto -> 
        ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(fromValue(contacto)))
      .switchIfEmpty(response404);
  }

  public Mono<ServerResponse> insertarContacto(ServerRequest request){
    Mono<Contacto> contactoMono = request.bodyToMono(Contacto.class);
    return contactoMono
      .flatMap( contacto -> contactoRepository.save(contacto)) 
        .flatMap( contactoGuardado -> ServerResponse.accepted()
          .contentType(MediaType.APPLICATION_JSON)
          .body(fromValue(contactoGuardado)))
      .switchIfEmpty(response406);
  }

  public Mono<ServerResponse> actualizarcontacto(ServerRequest request){
    Mono<Contacto> contactoMono = request.bodyToMono(Contacto.class);
    String id = request.pathVariable("id");
    Mono<Contacto> contactoActualizado = contactoMono.flatMap( contacto -> 
      contactoRepository.findById(id)
        .flatMap( oldContacto -> {
          oldContacto.setTelefono(contacto.getTelefono());
          oldContacto.setNombre(contacto.getNombre());
          oldContacto.setEmail(contacto.getEmail());
          return contactoRepository.save(oldContacto);
        }));
    return contactoActualizado.flatMap( contacto -> 
      ServerResponse.accepted()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromValue(contacto)))
      .switchIfEmpty(response404);
  }

  public Mono<ServerResponse> eliminarContacto(ServerRequest request){
    String id = request.pathVariable("id");
    Mono<Void> contactoEliminado = contactoRepository.deleteById(id);
    return ServerResponse.ok()
      .contentType(MediaType.APPLICATION_JSON)
      .body(contactoEliminado, Void.class);
  }
}
