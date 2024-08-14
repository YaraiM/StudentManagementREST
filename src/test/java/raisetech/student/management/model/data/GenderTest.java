package raisetech.student.management.model.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import raisetech.student.management.model.exception.InvalidEnumException;

class GenderTest {

  @Test
  void 有効な値でEnumが返されること() {
    assertEquals(Gender.男性, Gender.fromString("男性"));
    assertEquals(Gender.女性, Gender.fromString("女性"));
    assertEquals(Gender.その他, Gender.fromString("その他"));
  }

  @Test
  void 無効な値でInvalidEnumExceptionがスローされること() {
    InvalidEnumException exception = assertThrows(InvalidEnumException.class,
        () -> Gender.fromString("無効な値"));

    assertEquals("genderの入力値は「男性」「女性」「その他」のいずれかにしてください。入力値：無効な値",
        exception.getMessage());

  }

}
