package raisetech.student.management.model.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * リクエストパラメータに有効ではない入力形式で入力した場合にエラーメッセージを返すメソッドです。（バリデーション）
   *
   * @param ex 例外クラス（有効ではない入力形式）
   * @return 入力違反が発生したフィールドとエラーメッセージ
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMassage = error.getDefaultMessage();
      errors.put(fieldName, errorMassage);
    });

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "無効な入力形式です", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

  }

  /**
   * 存在しないIDをパラメータ指定した場合に例外処理を行うメソッドです。（検査例外）
   * ResourceNotFoundExceptionがスローされたとき、ステータスコード404（NotFound）および指定した例外メッセージを返します。
   *
   * @param ex 例外クラス（リソースが存在しない）
   * @return エラーレスポンス
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
        "リソースが見つかりません", Map.of("例外の詳細", ex.getMessage()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

}
