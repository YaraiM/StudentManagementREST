package raisetech.student.management.model.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 例外やエラーが発生した場合に返すレスポンスを定義するクラスです。 ステータスコードと例外／エラーメッセージを属性として保有しています。
 */
@Schema(description = "例外発生時のレスポンス")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String message;

}
