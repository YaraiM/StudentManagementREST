package raisetech.student.management.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import raisetech.student.management.model.data.Gender;

public class GenderValidator implements ConstraintValidator<ValidGender, Gender> {

  @Override
  public boolean isValid(Gender value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return value == Gender.男性 || value == Gender.女性 || value == Gender.その他;
  }

}
