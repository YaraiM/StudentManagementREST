package raisetech.student.management.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import raisetech.student.management.model.data.Status;

public class StatusValidator implements ConstraintValidator<ValidStatus, Status> {

  @Override
  public boolean isValid(Status value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return value == Status.仮申込 || value == Status.本申込 || value == Status.受講中
        || value == Status.受講終了;
  }

}
