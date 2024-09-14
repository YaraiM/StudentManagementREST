package raisetech.student.management.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static raisetech.student.management.model.data.Gender.その他;
import static raisetech.student.management.model.data.Gender.男性;
import static raisetech.student.management.model.data.Status.仮申込;
import static raisetech.student.management.model.data.Status.本申込;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;

@MybatisTest
@Transactional
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
    student.setGender(その他);
    student.setRemark("テスト");
    return student;
  }

  /**
   * テスト用に受講生コースのオブジェクトを生成するメソッドです。
   *
   * @param student 受講生
   * @return 受講生コース
   */
  private static StudentCourse createStudentCourse(Student student) {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(student.getId());
    studentCourse.setCourseName("AWS");
    studentCourse.setStartDate(LocalDateTime.of(2024, 4, 1, 9, 0, 0));
    studentCourse.setStartDate(LocalDateTime.of(2025, 4, 1, 9, 0, 0));
    return studentCourse;
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
  void 指定したIDに紐づく受講生コースの検索が行えること() {
    int id = 3;
    StudentCourse actual = sut.searchStudentCourse(id);
    assertEquals("Design", actual.getCourseName());
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
  void コース申込状況の全件検索が行えること() {
    List<CourseStatus> actual = sut.searchCourseStatusList();
    assertEquals(8, actual.size());
  }

  @Test
  void 指定した受講生コースIDに紐づくコース申込状況の検索が行えること() {
    int courseId = 5;
    CourseStatus actual = sut.searchCourseStatus(courseId);
    assertEquals(仮申込, actual.getStatus());
  }


  @Test
  void 受講生の新規登録ができること() {
    Student student = createStudent();
    sut.registerStudent(student);

    List<Student> actual = sut.searchStudents();
    assertEquals(6, actual.size());

  }

  @Test
  void 受講生コースの新規登録ができること() {
    Student student = createStudent();
    sut.registerStudent(student); // まず新たなidの受講生をDB登録し、外部キー制約違反が発生しないようにする

    StudentCourse studentCourse = createStudentCourse(student);
    sut.registerStudentCourses(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCoursesList();
    assertEquals(9, actual.size());

  }

  @Test
  void コース申込状況の新規登録ができること() {
    Student student = createStudent();
    sut.registerStudent(student); // まず新たなidの受講生をDB登録し、studentCourseの外部キー制約違反が発生しないようにする

    StudentCourse studentCourse = createStudentCourse(student);
    sut.registerStudentCourses(
        studentCourse); // 次に新たなidの受講生コースをDB登録し、courseStatusの外部キー制約違反が発生しないようにする

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(studentCourse.getId());
    courseStatus.setStatus(仮申込);
    sut.registerCourseStatus(courseStatus);

    List<CourseStatus> actual = sut.searchCourseStatusList();
    assertEquals(9, actual.size());

  }

  @Test
  void 受講生の更新ができること() {
    int id = 5;
    Student student = sut.searchStudent(id);
    student.setFullname("中村健太2");
    student.setFurigana("ナカムラケンタ2");
    student.setNickname("けん2");
    student.setMail("kenta2.nakamura@example.com");
    student.setAddress("北海道2");
    student.setAge(20);
    student.setGender(男性);
    student.setRemark("テスト2");
    student.setDeleted(true);

    sut.updateStudent(student);

    Student actual = sut.searchStudent(id);
    assertEquals("中村健太2", actual.getFullname());
    assertEquals("ナカムラケンタ2", actual.getFurigana());
    assertEquals("けん2", actual.getNickname());
    assertEquals("kenta2.nakamura@example.com", actual.getMail());
    assertEquals("北海道2", actual.getAddress());
    assertEquals(20, actual.getAge());
    assertEquals(男性, actual.getGender());
    assertEquals("テスト2", actual.getRemark());
    assertTrue(actual.isDeleted());

  }

  @Test
  void 受講生コースの更新ができること() {
    int studentId = 5;
    List<StudentCourse> studentCourses = sut.searchStudentCourses(studentId);
    studentCourses.get(0).setCourseName("AWS2");

    sut.updateStudentCourses(studentCourses.get(0));

    List<StudentCourse> actual = sut.searchStudentCourses(studentId);
    assertEquals("AWS2", actual.get(0).getCourseName());

  }

  @Test
  void コース申込状況の更新ができること() {
    int courseId = 1;
    CourseStatus courseStatus = sut.searchCourseStatus(courseId);
    courseStatus.setStatus(本申込);

    sut.updateCourseStatus(courseStatus);

    CourseStatus actual = sut.searchCourseStatus(courseId);
    assertEquals(本申込, actual.getStatus());

  }

}
