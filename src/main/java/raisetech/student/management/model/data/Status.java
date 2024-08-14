package raisetech.student.management.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import raisetech.student.management.model.exception.InvalidEnumException;

/**
 * コース申込状況のステータスの選択肢
 */
public enum Status {
  仮申込,
  本申込,
  受講中,
  受講終了;

  // JSONのリクエストボディで無効なEnum型のデータが入力された場合、HttpMessageNotReadableExceptionが発生するため、標準的なBean Validationでは対応できない。
  // したがって、カスタム例外（InvalidEnumException）を作成したうえで以下を設定し、適切なエラーメッセージがクライアントに表示されるようにする。
  @JsonCreator
  public static Status fromString(String value) throws InvalidEnumException {
    for (Status status : Status.values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new InvalidEnumException(
        "statusの入力値は「仮申込」「本申込」「受講中」「受講終了」のいずれかにしてください。入力値："
            + value);
  }

}
