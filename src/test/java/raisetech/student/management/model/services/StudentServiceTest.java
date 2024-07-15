package raisetech.student.management.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  //目的がStudentServiceの単体テストのため、repositoryとの連携を前提にしない。よってMock化。
  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  /**
   * テスト用に空の受講生詳細情報一覧を作成するメソッドです。コンバーターの代わりです。 deleted属性がtrueの受講生とfalseの受講生が一人ずつセットされています。
   *
   * @return　受講生詳細情報一覧
   */
  private static List<StudentDetail> createTestStudentDetails() {
    Student activeStudent = new Student();
    activeStudent.setDeleted(false);
    StudentCourse activeStudentCourse1 = new StudentCourse();
    StudentCourse activeStudentCourse2 = new StudentCourse();
    List<StudentCourse> activeStudentCourses = new ArrayList<>(
        List.of(activeStudentCourse1, activeStudentCourse2));
    StudentDetail activeStudentDetail = new StudentDetail(activeStudent, activeStudentCourses);

    Student deletedStudent = new Student();
    deletedStudent.setDeleted(true);
    StudentCourse deletedStudentCourse1 = new StudentCourse();
    StudentCourse deletedStudentCourse2 = new StudentCourse();
    List<StudentCourse> deletedStudentCourses = new ArrayList<>(
        List.of(deletedStudentCourse1, deletedStudentCourse2));
    StudentDetail deletedStudentDetail = new StudentDetail(deletedStudent, deletedStudentCourses);

    return new ArrayList<>(List.of(activeStudentDetail, deletedStudentDetail));
  }

  @BeforeEach
    //共通の事前準備を設定
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_引数deletedがnullの場合にリポジトリとコンバーターの処理を適切に呼び出し全件検索できること() {
    // 事前準備
    Boolean deleted = null;

    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);
    when(converter.convertStudentDetails(students, studentCoursesList)).thenReturn(studentDetails);

    // 実行
    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

    assertEquals(studentDetails, actualStudentDetails);
    assertEquals(2, actualStudentDetails.size());

  }

  @Test
  void 受講生詳細の一覧検索_引数deletedがfalseの場合にリポジトリとコンバーターの処理を適切に呼び出し現在の受講生詳細の一覧を検索できること() {
    // 事前準備
    Boolean deleted = false;

    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);
    when(converter.convertStudentDetails(students, studentCoursesList)).thenReturn(studentDetails);

    // 実行
    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

    assertEquals(1, actualStudentDetails.size());
    assertFalse(actualStudentDetails.get(0).getStudent().isDeleted());

  }

  @Test
  void 受講生詳細の一覧検索_引数deletedがtrueの場合にリポジトリとコンバーターの処理を適切に呼び出し過去の受講生詳細の一覧を検索できること() {
    // 事前準備
    Boolean deleted = true;

    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);
    when(converter.convertStudentDetails(students, studentCoursesList)).thenReturn(studentDetails);

    // 実行
    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

    assertEquals(1, actualStudentDetails.size());
    assertTrue(actualStudentDetails.get(0).getStudent().isDeleted());

  }

  @Test
  void 受講生詳細の検索_正常系_リポジトリの処理を適切に呼び出して受講生IDに紐づく受講生情報と受講生コース情報が返ってくること()
      throws ResourceNotFoundException {
    // 事前準備
    int id = 1;
    Student student = new Student();
    student.setId(id);

    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setStudentId(student.getId());
    studentCourses.add(studentCourse1);
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse2.setStudentId(student.getId());
    studentCourses.add(studentCourse2);

    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourses(student.getId())).thenReturn(studentCourses);

    // 実行
    StudentDetail result = sut.searchStudent(id);

    // 検証
    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourses(student.getId());
    assertNotNull(result);
    assertEquals(student, result.getStudent()); // result（StudentDetail）のStudent属性はstudentになっているか
    assertEquals(studentCourses, result.getStudentCourses());
    assertEquals(2, result.getStudentCourses().size());

    for (StudentCourse studentCourse : result.getStudentCourses()) {
      assertEquals(id, studentCourse.getStudentId());
    }

  }

  @Test
  void 受講生詳細の検索_異常系_存在しない受講生IDをメソッドに渡した場合に例外がスローされること()
      throws ResourceNotFoundException {
    // 事前準備
    int id = 777;
    Student student = new Student();
    when(repository.searchStudent(id)).thenReturn(null);

    // 実行と検証：searchStudent(777)を走らせたときにResourceNotFoundExceptionが発生するかどうか
    assertThrows(ResourceNotFoundException.class, () -> sut.searchStudent(id));

    verify(repository, times(1)).searchStudent(id);
    verify(repository, never()).searchStudentCourses(student.getId());
  }

  @Test
  void 受講生詳細情報の新規登録_リポジトリの処理を適切に呼び出したうえで受講生コース情報の初期情報が適切に登録されていること() {
    // 事前準備
    int id = 1;
    Student student = new Student();
    student.setId(id);

    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourses.add(studentCourse1);
    studentCourses.add(studentCourse2);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    doNothing().when(repository).registerStudent(any(Student.class));
    doNothing().when(repository).registerStudentCourses(any(StudentCourse.class));

    LocalDateTime testStartTime = LocalDateTime.now();

    // 実行
    StudentDetail result = sut.registerStudent(studentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(2)).registerStudentCourses(any(StudentCourse.class));
    assertNotNull(result);
    assertEquals(student, result.getStudent());
    assertEquals(2, result.getStudentCourses().size());

    for (StudentCourse studentCourse : result.getStudentCourses()) {
      assertEquals(id, studentCourse.getStudentId());
      assertTrue(
          studentCourse.getStartDate().isAfter(testStartTime) || studentCourse.getStartDate()
              .isEqual(testStartTime));
      assertTrue(
          studentCourse.getEndDate().isAfter(studentCourse.getStartDate()));
      assertEquals(1,
          ChronoUnit.YEARS.between(studentCourse.getStartDate(), studentCourse.getEndDate()));

    }

  }

  @Test
  void 受講生詳細情報の更新_リポジトリの処理を適切に呼び出していること() {
    // 事前準備
    Student student = new Student();

    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourses.add(studentCourse1);
    studentCourses.add(studentCourse2);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    doNothing().when(repository).updateStudent(any(Student.class));
    doNothing().when(repository).updateStudentCourses(any(StudentCourse.class));

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(2)).updateStudentCourses(any(StudentCourse.class));

  }

}
