package name.mrkandreev.demo.offlinelock.app;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AppHandler {
  private final AppService service;

  public AppHandler(AppService service) {
    this.service = service;
  }

  public Mono<ServerResponse> fetchAll(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(service.fetchAll(), LockDto.class));
  }

  public Mono<ServerResponse> fetchExpire(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            service.fetchExpire(request.pathVariable("key")), Long.class));
  }

  public Mono<ServerResponse> lock(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            service.lock(
                request.pathVariable("key"),
                Long.valueOf(request.pathVariable("duration"))
            ),
            LockDto.class));
  }

  public Mono<ServerResponse> refresh(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            service.refresh(
                request.pathVariable("key"),
                request.pathVariable("password"),
                Long.valueOf(request.pathVariable("duration"))
            ), Boolean.class));
  }

  public Mono<ServerResponse> unlock(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(service.unlock(
            request.pathVariable("key"),
            request.pathVariable("password")
        ), Boolean.class));
  }
}
