package name.mrkandreev.demo.offlinelock;

import static org.mockito.Mockito.when;


import name.mrkandreev.demo.offlinelock.app.AppService;
import name.mrkandreev.demo.offlinelock.app.LockDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class AppEndpointTest extends TestEnvironment {
  @Autowired
  WebTestClient webTestClient;

  @MockBean
  AppService service;

  // TODO: rest endpoint test
  @Test
  void fetchAll() {
    String key = "?key";
    String password = "?password";
    LockDto lockDto = new LockDto(key, password);
    when(service.fetchAll()).thenReturn(Flux.just(lockDto));

    LockDto[] responseLockDto = webTestClient
        .get().uri("/")
        .exchange()
        .expectStatus().isOk()
        .expectBody(LockDto[].class)
        .returnResult()
        .getResponseBody();

    Assertions.assertNotNull(responseLockDto);
    Assertions.assertEquals(lockDto, responseLockDto[0]);
  }

  @Test
  void fetchExpire() {
    String key = "myKey";
    Long expireAt = 1001L;
    when(service.fetchExpire(key)).thenReturn(Mono.just(expireAt));

    Long responseExpireAt = webTestClient
        .get().uri("/expire-at/" + key)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Long.class)
        .returnResult()
        .getResponseBody();

    Assertions.assertNotNull(responseExpireAt);
    Assertions.assertEquals(expireAt, responseExpireAt);
  }

  @Test
  void lock() {
    String key = "myKey";
    Long duration = 1000L;
    LockDto lockDto = new LockDto(key, "newPassword");
    when(service.lock(key, duration)).thenReturn(Mono.just(lockDto));

    LockDto responseLock = webTestClient
        .post().uri("/lock/" + key + "/" + duration)
        .exchange()
        .expectStatus().isOk()
        .expectBody(LockDto.class)
        .returnResult()
        .getResponseBody();

    Assertions.assertNotNull(responseLock);
    Assertions.assertEquals(lockDto, responseLock);
  }

  @Test
  void refresh() {
    String key = "myKey";
    String password = "newPassword";
    Long duration = 1000L;
    when(service.refresh(key, password, duration)).thenReturn(Mono.just(true));

    Boolean responseLock = webTestClient
        .post().uri("/refresh/" + key + "/" + password + "/" + duration)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class)
        .returnResult()
        .getResponseBody();

    Assertions.assertNotNull(responseLock);
    Assertions.assertTrue(responseLock);
  }

  @Test
  void unlock() {
    String key = "myKey";
    String password = "myPassword";
    when(service.unlock(key, password)).thenReturn(Mono.just(true));

    Boolean responseLock = webTestClient
        .post().uri("/unlock/" + key + "/" + password)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class)
        .returnResult()
        .getResponseBody();

    Assertions.assertNotNull(responseLock);
    Assertions.assertTrue(responseLock);
  }
}
