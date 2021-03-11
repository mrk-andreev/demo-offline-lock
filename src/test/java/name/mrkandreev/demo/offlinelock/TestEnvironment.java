package name.mrkandreev.demo.offlinelock;

import java.time.Duration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public class TestEnvironment {
  private static final Integer STARTUP_TIMEOUT = 600;
  private static final String IMAGE = "redis:6.2-alpine";
  private static final Integer PORT = 6379;

  private static GenericContainer<?> container;

  @DynamicPropertySource
  static synchronized void setupPostgresProperties(DynamicPropertyRegistry registry) {
    if (container == null) {
      container =
          new GenericContainer<>(IMAGE)
              .withExposedPorts(PORT)
              .withStartupTimeout(Duration.ofSeconds(STARTUP_TIMEOUT));
      container.start();
    }

    registry.add("spring.redis.host", container::getHost);
    registry.add("spring.redis.port", container::getFirstMappedPort);
  }

}
