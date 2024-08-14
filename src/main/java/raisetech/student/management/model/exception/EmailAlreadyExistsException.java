package raisetech.student.management.model.exception;

/**
 * すでに存在するメールアドレスを登録しようとすると発生する例外を定義するクラスです。引数にメッセージを指定すると、例外発生時にメッセージを返します。
 * Spring の @Transactional アノテーションは、デフォルトで非検査例外に対してロールバックを行うため、非検査例外としています。
 */
public class EmailAlreadyExistsException extends RuntimeException {

  public EmailAlreadyExistsException(String message) {
    super(message);
  }

}
