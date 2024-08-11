package raisetech.student.management.model.exception;

/**
 * リソースが存在しない場合の例外を定義するクラスです。引数にメッセージを指定すると、例外発生時にメッセージを返します。
 * Spring の @Transactional アノテーションは、デフォルトで非検査例外に対してロールバックを行うため、非検査例外としています。
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

}
