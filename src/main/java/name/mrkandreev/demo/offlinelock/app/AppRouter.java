package name.mrkandreev.demo.offlinelock.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppRouter {
  @Bean
  public RouterFunction<ServerResponse> fetchAll(AppHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.GET("/")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::fetchAll);
  }

  @Bean
  public RouterFunction<ServerResponse> fetchExpire(AppHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.GET("/expire-at/{key}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::fetchExpire);
  }

  @Bean
  public RouterFunction<ServerResponse> lock(AppHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.POST("/lock/{key}/{duration}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::lock);
  }

  @Bean
  public RouterFunction<ServerResponse> refresh(AppHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.POST("/refresh/{key}/{password}/{duration}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::refresh);
  }

  @Bean
  public RouterFunction<ServerResponse> unlock(AppHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.POST("/unlock/{key}/{password}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::unlock);
  }
}
