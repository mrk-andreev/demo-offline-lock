package name.mrkandreev.demo.offlinelock.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  @ConfigurationProperties(prefix = "app")
  public AppProperties appProperties() {
    return new AppProperties();
  }
}
