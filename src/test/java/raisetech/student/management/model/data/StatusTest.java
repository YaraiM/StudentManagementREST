package raisetech.student.management.model.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import raisetech.student.management.model.exception.InvalidEnumException;

class StatusTest {

  @Test
  void 有効な値でEnumが返されること() {
    assertEquals(Status.仮申込, Status.fromString("仮申込"));
    assertEquals(Status.本申込, Status.fromString("本申込"));
    assertEquals(Status.受講中, Status.fromString("受講中"));
    assertEquals(Status.受講終了, Status.fromString("受講終了"));
  }

  @Test
  void 無効な値でInvalidEnumExceptionがスローされること() {
    InvalidEnumException exception = assertThrows(InvalidEnumException.class,
        () -> Status.fromString("無効な値"));

    assertEquals(
        "statusの入力値は「仮申込」「本申込」「受講中」「受講終了」のいずれかにしてください。入力値：無効な値",
        exception.getMessage());

  }

}
