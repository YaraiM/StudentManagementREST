package raisetech.student.management.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * コース申込情報のオブジェクトです。
 */
@Schema(description = "コース申込情報")
@Getter
@Setter
public class CourseStatus {

  private int id;

  private int courseId;

  @NotNull
  private Status status;

}
