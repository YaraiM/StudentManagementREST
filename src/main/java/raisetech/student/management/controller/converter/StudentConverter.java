package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;

/**
 * 受講生一覧と受講生コース一覧を受講生の詳細情報一覧に変換するコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生のIDに紐づく受講生コース情報をマッピングしています。 受講生コース情報は受講生に対して複数存在するため、ループ処理で受講生の詳細情報を組み立てています。
   *
   * @param students        受講生の一覧
   * @param studentsCourses 受講生のコースの一覧
   * @return 受講生の詳細情報
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourse> studentsCourses) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);
      List<StudentCourse> convertStudentsCourses = studentsCourses.stream()
          .filter(studentCourse -> studentCourse.getStudentId() == student.getId())
          .collect(Collectors.toList());
      studentDetail.setStudentCourses(convertStudentsCourses);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
