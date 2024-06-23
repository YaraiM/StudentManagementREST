package raisetech.student.management.model.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;

@Getter
@Setter
public class StudentDetail {

  private Student student;
  private List<StudentCourse> studentCourse;

}
