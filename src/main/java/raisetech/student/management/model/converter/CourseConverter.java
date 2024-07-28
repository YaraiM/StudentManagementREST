package raisetech.student.management.model.converter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.CourseDetail;

@Component
public class CourseConverter {

  public List<CourseDetail> convertCourseDetails(List<StudentCourse> studentCoursesList,
      List<CourseStatus> courseStatusesList) {
    List<CourseDetail> courseDetails = new ArrayList<>();

    for (StudentCourse studentCourse : studentCoursesList) {
      CourseDetail courseDetail = new CourseDetail();

      courseDetail.setStudentCourse(studentCourse);

      for (CourseStatus courseStatus : courseStatusesList) {
        if (courseStatus.getCourseId() == studentCourse.getId()) {
          courseDetail.setCourseStatus(courseStatus);
        }
      }

      courseDetails.add(courseDetail);

    }

    return courseDetails;
  }

}
