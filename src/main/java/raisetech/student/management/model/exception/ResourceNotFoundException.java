package raisetech.student.management.model.exception;

/**
 * リソースが存在しない場合の例外を定義するクラスです。検査例外です。 引数にメッセージを指定すると、例外発生時にメッセージを返します。
 */
public class ResourceNotFoundException extends Exception {

  public ResourceNotFoundException(String message) {
    super(message);
  }

}
