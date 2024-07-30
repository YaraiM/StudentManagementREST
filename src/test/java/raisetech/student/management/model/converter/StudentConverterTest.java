package raisetech.student.management.model.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;

@ExtendWith(MockitoExtension.class)
class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void before() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生一覧および受講生コース一覧から受講生IDが一致する情報を抽出して受講生詳細情報に変換して一覧にできていること() {
    // 事前準備
    List<Student> students = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      Student student = new Student();
      student.setId(i);
      students.add(student);
    }

    List<StudentCourse> studentCoursesList = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      StudentCourse studentCourse = new StudentCourse();
      studentCourse.setStudentId(i);
      studentCoursesList.add(studentCourse); //受講生IDが同じコースを2つずつセットして検証する
      studentCoursesList.add(studentCourse);
    }

    // 実行
    List<StudentDetail> actualStudentDetails = sut.convertStudentDetails(students,
        studentCoursesList);

    // 検証
    assertEquals(2, actualStudentDetails.size());

    for (StudentDetail detail : actualStudentDetails) {
      assertEquals(2, detail.getStudentCourses().size());
      for (StudentCourse course : detail.getStudentCourses()) {
        assertEquals(course.getStudentId(), detail.getStudent().getId());
      }
    }
  }
}
