package raisetech.student.management.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * コース申込情報のオブジェクトです。
 */
@Schema(description = "コース申込情報")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatus {

  private int id;

  private int courseId;

  @NotNull
  private Status status;

}
