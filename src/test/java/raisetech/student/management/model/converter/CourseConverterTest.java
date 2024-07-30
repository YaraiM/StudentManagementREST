package raisetech.student.management.model.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.CourseDetail;

@ExtendWith(MockitoExtension.class)
class CourseConverterTest {

  private CourseConverter sut;

  @BeforeEach
  void before() {
    sut = new CourseConverter();
  }

  @Test
  void 受講生コース一覧およびコース申込状況一覧から受講生コースIDが一致する情報を抽出して受講生コース詳細情報に変換して一覧にできていること() {
    // 事前準備
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      StudentCourse studentCourse = new StudentCourse();
      studentCourse.setId(i);
      studentCoursesList.add(studentCourse);
    }

    List<CourseStatus> courseStatusesList = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      CourseStatus courseStatus = new CourseStatus();
      courseStatus.setCourseId(i);
      courseStatusesList.add(courseStatus);
    }

    // 実行
    List<CourseDetail> actualCourseDetails = sut.convertCourseDetails(studentCoursesList,
        courseStatusesList);

    // 検証
    assertEquals(2, actualCourseDetails.size());

    for (CourseDetail detail : actualCourseDetails) {
      assertEquals(detail.getStudentCourse().getId(),
          detail.getCourseStatus().getCourseId());
    }
  }
}
