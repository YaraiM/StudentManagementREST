package raisetech.student.management.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.StudentCourse;

@Schema(description = "受講生コース詳細情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {

  @Valid
  private StudentCourse studentCourse;

  @Valid
  private CourseStatus courseStatus;

}
