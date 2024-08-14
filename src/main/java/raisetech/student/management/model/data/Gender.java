package raisetech.student.management.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import raisetech.student.management.model.exception.InvalidEnumException;

/**
 * 受講生情報の性別の選択肢
 */
public enum Gender {
  男性,
  女性,
  その他;

  // JSONのリクエストボディで無効なEnum型のデータが入力された場合、HttpMessageNotReadableExceptionが発生するため、標準的なBean Validationでは対応できない。
  // したがって、カスタム例外（InvalidEnumException）を作成したうえで以下を設定し、適切なエラーメッセージがクライアントに表示されるようにする。
  @JsonCreator
  public static Gender fromString(String value) throws InvalidEnumException {
    for (Gender gender : Gender.values()) {
      if (gender.name().equalsIgnoreCase(value)) {
        return gender;
      }
    }
    throw new InvalidEnumException(
        "genderの入力値は「男性」「女性」「その他」のいずれかにしてください。入力値：" + value);
  }

}
