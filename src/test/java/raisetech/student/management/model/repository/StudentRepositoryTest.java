package raisetech.student.management.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること() {
    List<Student> actual = sut.searchStudents();
    assertEquals(5, actual.size());
  }

  @Test
  void 指定したIDに紐づく受講生の検索が行えること() {
    int id = 3;
    Student actual = sut.searchStudent(id);
    assertEquals("鈴木一郎", actual.getFullname());
  }

  @Test
  void 受講生コースの全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCoursesList();
    assertEquals(8, actual.size());
  }

  @Test
  void 指定した受講生IDに紐づく受講生コースの検索が行えること() {
    int studentId = 3;
    List<StudentCourse> actual = sut.searchStudentCourses(studentId);
    assertEquals(2, actual.size());
    assertEquals("Python", actual.get(0).getCourseName());
    assertEquals("Java", actual.get(1).getCourseName());
  }


}
