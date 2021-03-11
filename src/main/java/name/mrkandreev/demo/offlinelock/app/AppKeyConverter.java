package name.mrkandreev.demo.offlinelock.app;

import org.springframework.stereotype.Component;

@Component
public class AppKeyConverter {
  private final AppProperties properties;

  public AppKeyConverter(AppProperties properties) {
    this.properties = properties;
  }

  /**
   * Convert key to storage specific format.
   *
   * @param key - Lock key.
   * @return transformed key to storage specific format.
   */
  public String toInternal(String key) {
    return properties.getLockPrefix() + key;
  }

  public String toPublic(String key) {
    return key.substring(properties.getLockPrefix().length());
  }

  /**
   * Convert to placeholder that fulfill any key.
   *
   * @return placeholder
   */
  public String anyKeyFilter() {
    return String.format("%s*", properties.getLockPrefix());
  }
}
