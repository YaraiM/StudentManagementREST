package raisetech.student.management.model.exception;

/**
 * Enum型のフィールドに、許容されていない値を入寮した場合の例外を定義するクラスです。引数にメッセージを指定すると、例外発生時にメッセージを返します。
 * Spring の @Transactional アノテーションは、デフォルトで非検査例外に対してロールバックを行うため、非検査例外としています。
 */
public class InvalidEnumException extends RuntimeException {

  public InvalidEnumException(String message) {
    super(message);
  }

}
