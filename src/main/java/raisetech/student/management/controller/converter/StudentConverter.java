package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.model.data.CourseStatus;
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
   * @param students           受講生の一覧
   * @param studentCoursesList 受講生のコースの一覧
   * @param courseStatusesList コースの申込状況の一覧
   * @return 受講生の詳細情報
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourse> studentCoursesList, List<CourseStatus> courseStatusesList) {
    List<StudentDetail> studentDetails = new ArrayList<>();

    for (Student student : students) {
      StudentDetail studentDetail = new StudentDetail();

      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCourses = studentCoursesList.stream()
          .filter(studentCourse -> studentCourse.getStudentId() == student.getId())
          .collect(Collectors.toList());
      studentDetail.setStudentCourses(convertStudentCourses);

      // TODO:要動作確認。studentCoursesとcourseStatusが正しく紐づいているか。
      List<CourseStatus> convertCourseStatuses = courseStatusesList.stream()
          .filter(courseStatus -> convertStudentCourses.stream()
              .anyMatch(studentCourse -> studentCourse.getId() == courseStatus.getCourseId()))
          .collect(Collectors.toList());
      studentDetail.setCourseStatuses(convertCourseStatuses);

      studentDetails.add(studentDetail);

    }

    return studentDetails;
  }
}
