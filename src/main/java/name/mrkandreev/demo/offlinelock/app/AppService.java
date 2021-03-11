package name.mrkandreev.demo.offlinelock.app;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AppService {
  private final ReactiveRedisOperations<String, String> redisOperations;
  private final AppKeyConverter keyConverter;

  public AppService(ReactiveRedisOperations<String, String> redisOperations,
                    AppKeyConverter keyConverter) {
    this.redisOperations = redisOperations;
    this.keyConverter = keyConverter;
  }

  /**
   * List all active locks.
   *
   * @return Locks
   */
  public Flux<LockDto> fetchAll() {
    return redisOperations.keys(keyConverter.anyKeyFilter())
        .map(key -> new LockDto(keyConverter.toPublic(key)));
  }

  /**
   * Fetch expire time for specific key
   *
   * @param key - Lock key
   * @return - Expire at
   */
  public Mono<Long> fetchExpire(String key) {
    return redisOperations.getExpire(keyConverter.toInternal(key))
        .map(Duration::toMillis);
  }

  /**
   * Accurate lock
   *
   * @param key      - Lock key.
   * @param duration - Lock duration.
   * @return - Lock representation.
   */
  public Mono<LockDto> lock(String key, Long duration) {
    String password = nextPassword();
    return redisOperations.opsForValue()
        .setIfAbsent(keyConverter.toInternal(key), password, convertDuration(duration))
        .flatMap(isSuccess -> {
          if (isSuccess) {
            return Mono.just(new LockDto(key, password));
          } else {
            return Mono.error(new LockException());
          }
        });
  }

  /**
   * Refresh lock
   *
   * @param key      - Lock key
   * @param password - Lock password
   * @param duration - Lock duration
   * @return approve
   */
  public Mono<Boolean> refresh(String key, String password, Long duration) {
    return redisOperations.opsForValue()
        .get(keyConverter.toInternal(key))
        .switchIfEmpty(Mono.error(new LockException()))
        .flatMap(s -> {
          if (s.equals(password)) {
            return redisOperations.opsForValue()
                .set(keyConverter.toInternal(key), password, convertDuration(duration));
          } else {
            return Mono.error(new LockException());
          }
        });
  }

  /**
   * Release lock
   *
   * @param key      - Lock key
   * @param password - Lock password
   * @return approve
   */
  public Mono<Boolean> unlock(String key, String password) {
    return redisOperations.opsForValue()
        .get(keyConverter.toInternal(key))
        .flatMap(s -> {
          if (s.equals(password)) {
            return redisOperations.delete(keyConverter.toInternal(key))
                .map(count -> count > 0);
          } else {
            return Mono.error(new LockException());
          }
        });
  }

  /**
   * Generate next non crypto password.
   *
   * @return password
   */
  private String nextPassword() {
    return UUID.randomUUID().toString();
  }

  /**
   * Convert duration from public format to internal
   *
   * @param duration - Duration in Millis.
   * @return Duration
   */
  private Duration convertDuration(Long duration) {
    return Duration.ofMillis(duration);
  }
}
