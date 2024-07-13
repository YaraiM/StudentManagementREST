package raisetech.student.management.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @BeforeEach
    //共通の事前準備を設定
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理を適切に呼び出せていること() {
    // 事前準備
    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);

    // 実行
    sut.searchStudentList();

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

  }

  @Test
  void 過去の受講生詳細の一覧検索_リポジトリとコンバーターの処理を適切に呼び出せていること() {
    // 事前準備
    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);

    // 実行
    sut.searchPastStudentList();

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

  }

  @Test
  void 過去の受講生詳細の一覧検索_deleted属性がtrueの受講生のみ返されていること() {
    // 事前準備：現在の受講生と過去の受講生の定義
    Student activeStudent = new Student();
    activeStudent.setDeleted(false);
    Student deletedStudent = new Student();
    deletedStudent.setDeleted(true);

    // 事前準備：現在の受講生と過去の受講生が一人ずつ含まれる受講生一覧および空の受講生コース一覧を定義し、返り値を指定
    List<Student> students = new ArrayList<>(List.of(activeStudent, deletedStudent));
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);

    // 事前準備：現在の受講生詳細情報と過去の受講生詳細を定義
    StudentDetail activeStudentDetail = new StudentDetail();
    activeStudentDetail.setStudent(activeStudent);
    StudentDetail deletedStudentDetail = new StudentDetail();
    deletedStudentDetail.setStudent(deletedStudent);

    // 事前準備：受講生情報一覧と受講生コース情報一覧をコンバーターに渡したときの返り値を定義
    when(converter.convertStudentDetails(students, studentCoursesList)).thenReturn(
        List.of(activeStudentDetail, deletedStudentDetail));

    // 実行：StudentServiceの実装に従い実行されるが、入力されるデータは事前準備で定義した内容が使用される。
    List<StudentDetail> result = sut.searchPastStudentList();

    // 検証：resultに、deleted属性がtrueのactiveStudentDetailの１件のみが含まれているかを検証
    assertEquals(1, result.size());
    assertTrue(result.get(0).getStudent().isDeleted());

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

    // 検証 TODO:studentCoursesのすべての要素におけるstudentIdがidに一致しているかを検証する
    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourses(student.getId());
    assertNotNull(result);
    assertEquals(student, result.getStudent()); // result（StudentDetail）のStudent属性はstudentになっているか
    assertEquals(studentCourses, result.getStudentCourses());
    assertEquals(2, result.getStudentCourses().size());
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

}
