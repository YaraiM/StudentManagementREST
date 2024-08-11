package raisetech.student.management.model.exception;

/**
 * Enum型のフィールドに、許容されていない値を入寮した場合の例外を定義するクラスです。引数にメッセージを指定すると、例外発生時にメッセージを返します。
 */
public class InvalidEnumException extends Exception {

  public InvalidEnumException(String message) {
    super(message);
  }

}
