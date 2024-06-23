package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;

@Component
public class StudentConverter {

  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourse> studentsCourses) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);
      List<StudentCourse> convertStudentsCourses = studentsCourses.stream()
          .filter(studentCourse -> studentCourse.getStudentId() == student.getId())
          .collect(Collectors.toList());
      studentDetail.setStudentCourse(convertStudentsCourses);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
