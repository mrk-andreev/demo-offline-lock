package name.mrkandreev.demo.offlinelock.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LockDtoTest {
  @Test
  void setKey() {
    String oldKey = "oldKey";
    String newKey = "newKey";

    LockDto dto = new LockDto(oldKey);
    dto.setKey(newKey);

    Assertions.assertEquals(newKey, dto.getKey());
  }

  @Test
  void setPassword() {
    String oldPassword = "oldPassword";
    String newPassword = "newPassword";

    LockDto dto = new LockDto("_", oldPassword);
    dto.setPassword(newPassword);

    Assertions.assertEquals(newPassword, dto.getPassword());
  }

  @Test
  void isEquals() {
    String key = "myKey";
    String password = "myPassword";

    LockDto dto1 = new LockDto(key, password);
    LockDto dto2 = new LockDto(key, password);

    Assertions.assertEquals(dto1, dto2);
  }

  @Test
  void isEqualsSelf() {
    String key = "myKey";
    String password = "myPassword";

    LockDto dto = new LockDto(key, password);

    Assertions.assertEquals(dto, dto);
  }

  @Test
  void isEqualsNull() {
    String key = "myKey";
    String password = "myPassword";

    LockDto dto = new LockDto(key, password);

    Assertions.assertFalse(dto.equals(null));
  }

  @Test
  void equalsHashcode() {
    String key = "myKey";
    String password = "myPassword";

    LockDto dto1 = new LockDto(key, password);
    LockDto dto2 = new LockDto(key, password);

    Assertions.assertEquals(dto1.hashCode(), dto2.hashCode());
  }
}
