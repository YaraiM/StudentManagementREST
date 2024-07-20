package raisetech.student.management.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.model.data.Gender;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  /**
   * テスト用に受講生のオブジェクトを生成するメソッドです。
   *
   * @return 受講生
   */
  private static Student createStudent() {
    Student student = new Student();
    student.setFullname("荒川亜土夢");
    student.setFurigana("あらかわあとむ");
    student.setNickname("アトム");
    student.setMail("atom@example.com");
    student.setAddress("沖縄");
    student.setAge(18);
    student.setGender(Gender.valueOf("その他"));
    student.setRemark("テスト");
    return student;
  }

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

  @Test
  void 受講生の新規登録ができること() {
    Student student = createStudent();

    sut.registerStudent(student);

    List<Student> actual = sut.searchStudents();
    assertEquals(6, actual.size());
    assertEquals("荒川亜土夢", actual.get(5).getFullname());

  }

  @Test
  void 受講生コースの新規登録ができること() {
    Student student = createStudent();
    sut.registerStudent(student); // まずidが6の受講生をDB登録し、外部キー制約違反が発生しないようにする

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(6);
    studentCourse.setCourseName("AWS9");
    studentCourse.setStartDate(LocalDateTime.of(2024, 4, 1, 9, 0, 0));
    studentCourse.setStartDate(LocalDateTime.of(2025, 4, 1, 9, 0, 0));

    sut.registerStudentCourses(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCoursesList();
    assertEquals(9, actual.size());
    assertEquals("AWS9", actual.get(8).getCourseName());

  }

}
