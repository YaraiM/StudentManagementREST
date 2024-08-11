package raisetech.student.management.model.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * リクエストパラメータに有効ではない入力形式で入力した場合にエラーメッセージを返すメソッドです。（バリデーション）
   *
   * @param ex 例外クラス（有効ではない入力形式）
   * @return 入力違反が発生したフィールドとエラーメッセージ
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    List<Map<String, String>> errors = new ArrayList<>();

    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      Map<String, String> error = new HashMap<>();
      error.put("field", fieldError.getField());
      error.put("message", fieldError.getDefaultMessage());
      errors.add(error);
    });

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
        "バリデーションエラーです。",
        errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

  }

  /**
   * Enum型のリクエストパラメータとして有効ではない値を入力してリクエスト場合にエラーメッセージを返すメソッドです。
   *
   * @param ex 例外クラス（有効ではない入力形式）
   * @return エラーレスポンス
   */
  @ExceptionHandler(InvalidEnumException.class)
  public ResponseEntity<ErrorResponse> handleInvalidEnumException(
      InvalidEnumException ex) {

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * 存在しないIDをパラメータ指定した場合に例外処理を行うメソッドです。（検査例外）
   * ResourceNotFoundExceptionがスローされたとき、ステータス（NotFound）および指定した例外メッセージを返します。
   *
   * @param ex 例外クラス（リソースが存在しない）
   * @return エラーレスポンス
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

}
