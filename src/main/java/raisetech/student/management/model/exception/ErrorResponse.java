package raisetech.student.management.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 例外やエラーが発生した場合に返すレスポンスを定義するクラスです。 ステータスコードと例外／エラーメッセージを属性として保有しています。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String message;

}
