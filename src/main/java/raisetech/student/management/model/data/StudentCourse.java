package raisetech.student.management.model.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生のコース情報
 */
@Getter
@Setter
public class StudentCourse {

  private int id;
  private int studentId;
  private String courseName;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  
}
