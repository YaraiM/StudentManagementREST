package raisetech.student.management.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 受講生のコース情報のオブジェクトです。
 */
@Schema(description = "受講生コース情報")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourse {

  private int id;

  private int studentId;

  @NotBlank
  private String courseName;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

}
