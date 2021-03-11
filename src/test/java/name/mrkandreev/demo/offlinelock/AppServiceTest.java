package name.mrkandreev.demo.offlinelock;

import java.time.Duration;
import java.util.UUID;
import name.mrkandreev.demo.offlinelock.app.AppKeyConverter;
import name.mrkandreev.demo.offlinelock.app.AppService;
import name.mrkandreev.demo.offlinelock.app.LockDto;
import name.mrkandreev.demo.offlinelock.app.LockException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class AppServiceTest extends TestEnvironment {
  @Autowired
  AppService appService;

  @Autowired
  AppKeyConverter keyConverter;

  @Autowired
  ReactiveRedisOperations<String, String> redisOperations;

  @Test
  void fetchAll() {
    String key = UUID.randomUUID().toString();

    redisOperations.opsForValue()
        .set(keyConverter.toInternal(key), "-")
        .block();

    Assertions.assertTrue(
        appService.fetchAll()
            .any(lockDto -> lockDto.getKey().equals(key))
            .block()
    );
  }

  @Test
  void fetchExpire() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);

    redisOperations.opsForValue()
        .set(keyConverter.toInternal(key), "-", duration)
        .block();

    long expireAt = appService.fetchExpire(key).block();
    Assertions.assertTrue(duration.toMillis() - expireAt < 1000);
  }

  @Test
  void lock() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);

    LockDto lockDto = appService.lock(key, duration.toMillis()).block();

    Assertions.assertEquals(key, lockDto.getKey());
    Assertions.assertNotNull(lockDto.getPassword());
    Assertions.assertNotNull(appService.fetchExpire(key).block());
  }

  @Test
  void lockDouble() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);
    appService.lock(key, duration.toMillis()).block();

    Assertions
        .assertThrows(LockException.class, () -> appService.lock(key, duration.toMillis()).block());
  }

  @Test
  void refresh() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);
    LockDto lockDto = appService.lock(key, duration.toMillis()).block();

    boolean isSuccess = appService.refresh(key, lockDto.getPassword(), duration.toMillis()).block();

    Assertions.assertTrue(isSuccess);
  }

  @Test
  void refreshInvalidPassword() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);
    appService.lock(key, duration.toMillis()).block();

    // TODO: check this test
    Assertions
        .assertThrows(LockException.class,
            () -> appService.refresh(key, "-", duration.toMillis()).block());
  }

  @Test
  void unlock() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);
    LockDto lockDto = appService.lock(key, duration.toMillis()).block();

    Assertions.assertTrue(appService.unlock(key, lockDto.getPassword()).block());
    Assertions.assertNull(appService.fetchExpire(key).block());
  }

  @Test
  void unlockInvalidPassword() {
    String key = UUID.randomUUID().toString();
    Duration duration = Duration.ofSeconds(100L);
    appService.lock(key, duration.toMillis()).block();

    Assertions.assertThrows(LockException.class,
        () -> appService.unlock(key, "-").block());
  }

}
