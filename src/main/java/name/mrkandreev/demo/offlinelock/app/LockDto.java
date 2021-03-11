package name.mrkandreev.demo.offlinelock.app;

import java.util.Objects;

public class LockDto {
  private String key;

  private String password;

  public LockDto(String key) {
    this.key = key;
  }

  public LockDto(String key, String password) {
    this.key = key;
    this.password = password;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LockDto lockDto = (LockDto) o;
    return Objects.equals(key, lockDto.key) &&
        Objects.equals(password, lockDto.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, password);
  }
}
