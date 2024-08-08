package raisetech.student.management.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {

  String message() default "申込状況は「仮申込」「本申込」「受講中」「受講終了」のいずれかを入力してください";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
