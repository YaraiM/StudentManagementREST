package raisetech.student.management.model.data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生のコース情報のオブジェクトです。
 */
@Getter
@Setter
public class StudentCourse {

  private int id;

  private int studentId;

  @NotBlank
  private String courseName;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

}
