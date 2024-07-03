package raisetech.student.management.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 存在しないIDをパラメータ指定した場合に例外処理を行うメソッドです。（検査例外）
   * ResourceNotFoundExceptionがスローされたとき、ステータスコード404（NotFound）および指定した例外メッセージを返します。
   *
   * @param ex 例外クラス（リソースが存在しない）
   * @return エラーレスポンス
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * 開発者が想定していない例外が発生した場合に共通して実行されるメソッドです。
   *
   * @param ex 例外クラス（想定しない例外）
   * @return エラーレスポンス
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "予期せぬエラーが発生しました。");
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
