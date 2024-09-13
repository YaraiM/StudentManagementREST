package raisetech.student.management.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.model.converter.CourseConverter;
import raisetech.student.management.model.converter.StudentConverter;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Gender;
import raisetech.student.management.model.data.Status;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.IntegratedDetail;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.EmailAlreadyExistsException;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  //目的がStudentServiceの単体テストのため、repositoryとの連携を前提にしない。よってMock化。
  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter studentConverter;

  @Mock
  private CourseConverter courseConverter;

  @Autowired
  private StudentService sut;

  /**
   * テスト用に受講生詳細情報一覧を作成するメソッドです。コンバーターの代わりです。
   *
   * @return　受講生詳細情報一覧
   */
  private static List<StudentDetail> createTestStudentDetails() {
    Student student1 = new Student(555, "田中太郎", "たなかたろう", "たなっち",
        "tanaka@example.com", "東京", 21, Gender.男性, "テスト用", false);

    StudentCourse studentCourse1 = new StudentCourse(666, 555, "Java",
        LocalDateTime.of(2024, 7, 1, 0, 0, 0),
        LocalDateTime.of(2025, 7, 1, 0, 0, 0));
    StudentCourse studentCourse2 = new StudentCourse(777, 555, "Python",
        LocalDateTime.of(2024, 9, 1, 0, 0, 0),
        LocalDateTime.of(2025, 9, 1, 0, 0, 0));
    List<StudentCourse> studentCourses1 = new ArrayList<>(
        List.of(studentCourse1, studentCourse2));
    StudentDetail studentDetail1 = new StudentDetail(student1, studentCourses1);

    Student student2 = new Student(666, "鈴木花子", "すすきはなこ", "すずっち",
        "suzuki@example.com", "大阪", 37, Gender.女性, "テスト用", true);

    StudentCourse studentCourse3 = new StudentCourse(888, 666, "Design",
        LocalDateTime.of(2023, 7, 1, 0, 0, 0),
        LocalDateTime.of(2024, 7, 1, 0, 0, 0));
    StudentCourse studentCourse4 = new StudentCourse(999, 666, "Front",
        LocalDateTime.of(2023, 9, 1, 0, 0, 0),
        LocalDateTime.of(2024, 9, 1, 0, 0, 0));
    List<StudentCourse> studentCourses2 = new ArrayList<>(
        List.of(studentCourse3, studentCourse4));
    StudentDetail studentDetail2 = new StudentDetail(student2, studentCourses2);

    return new ArrayList<>(List.of(studentDetail1, studentDetail2));

  }

  /**
   * テスト用に受講生コース詳細情報一覧を作成するメソッドです。コンバーターの代わりです。
   *
   * @return　受講生コース詳細情報一覧
   */
  private static List<CourseDetail> createTestCourseDetails() {
    StudentCourse studentCourse1 = new StudentCourse(666, 555, "Java",
        LocalDateTime.of(2024, 7, 1, 0, 0, 0),
        LocalDateTime.of(2025, 7, 1, 0, 0, 0));
    StudentCourse studentCourse2 = new StudentCourse(777, 555, "Python",
        LocalDateTime.of(2024, 9, 1, 0, 0, 0),
        LocalDateTime.of(2025, 9, 1, 0, 0, 0));

    CourseStatus courseStatus1 = new CourseStatus(111, 666, Status.受講中);
    CourseStatus courseStatus2 = new CourseStatus(222, 777, Status.本申込);

    CourseDetail courseDetail1 = new CourseDetail(studentCourse1, courseStatus1);
    CourseDetail courseDetail2 = new CourseDetail(studentCourse2, courseStatus2);

    return new ArrayList<>(List.of(courseDetail1, courseDetail2));

  }

  @BeforeEach
  void before() {
    sut = new StudentService(repository, studentConverter, courseConverter);
  }

  @ParameterizedTest
  @MethodSource("provideStudentTestCases")
  void 受講生詳細の一覧検索_引数に応じて適切にフィルタリングが行われること(
      StudentSearchCriteria criteria, int expectedResultCount) {

    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.searchStudents()).thenReturn(students);
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);
    when(studentConverter.convertStudentDetails(students, studentCoursesList)).thenReturn(
        studentDetails);

    // 実行
    List<StudentDetail> actualStudentDetails = sut.searchStudentList(criteria);

    // 検証
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(studentConverter, times(1)).convertStudentDetails(students, studentCoursesList);

    assertEquals(expectedResultCount, actualStudentDetails.size());

  }

  /**
   * 受講生詳細一覧検索のパラメータテストに適用するテストケースです。
   *
   * @return Argument
   */
  private static Stream<Arguments> provideStudentTestCases() {
    return Stream.of(
        // リクエストパラメータなし。全件検索が行われるケース。
        Arguments.of(new StudentSearchCriteria(null, null, null,
            null, null, null, null, null, null, null,
            null, null, null, null), 2),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new StudentSearchCriteria("田中太郎", "たなかたろう",
            "たなっち", "tanaka@example.com", "東京", 10, 30,
            Gender.男性, false, "Java",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 1)), 1),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つも存在しないケース。
        Arguments.of(new StudentSearchCriteria("鈴木太郎", "たなかたろう",
            "たなっち", "tanaka@example.com", "東京", 10, 30,
            Gender.男性, false, "Java",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 1)), 0));

  }

  @ParameterizedTest
  @MethodSource("provideCourseTestCases")
  void 受講生コース詳細の一覧検索_引数に応じて適切に条件検索が行われること(
      CourseSearchCriteria criteria, int expectedResultCount) {

    List<StudentCourse> studentCoursesList = new ArrayList<>();
    List<CourseStatus> courseStatusList = new ArrayList<>();
    List<CourseDetail> courseDetails = createTestCourseDetails();

    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);
    when(repository.searchCourseStatusList()).thenReturn(courseStatusList);
    when(courseConverter.convertCourseDetails(studentCoursesList, courseStatusList)).thenReturn(
        courseDetails);

    // 実行
    List<CourseDetail> actualCourseDetails = sut.searchStudentCourseList(criteria);

    // 検証
    verify(repository, times(1)).searchStudentCoursesList();
    verify(repository, times(1)).searchCourseStatusList();
    verify(courseConverter, times(1)).convertCourseDetails(studentCoursesList, courseStatusList);

    assertEquals(expectedResultCount, actualCourseDetails.size());

  }

  /**
   * 受講生コース詳細一覧検索のパラメータテストに適用するテストケースです。
   *
   * @return Argument
   */
  private static Stream<Arguments> provideCourseTestCases() {
    return Stream.of(
        // リクエストパラメータなし。全件検索が行われるケース。
        Arguments.of(new CourseSearchCriteria(null,
            null, null, null, null, null), 2),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new CourseSearchCriteria("Java",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 1), Status.受講中), 1),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つも存在しないケース。
        Arguments.of(new CourseSearchCriteria("Java",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 1), Status.受講終了), 0));

  }

  @Test
  void 受講生詳細の検索_正常系_リポジトリの処理を適切に呼び出して受講生IDに紐づく受講生情報と受講生コース情報が返ってくること() {
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
  void 受講生詳細の検索_異常系_存在しない受講生IDをメソッドに渡した場合に例外がスローされること() {
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
  void 受講生コース詳細の検索_正常系_リポジトリの処理を適切に呼び出して受講生コースIDに紐づくコース申込状況と受講生コース情報が返ってくること() {

    int id = 555;
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(studentCourse.getId());

    when(repository.searchStudentCourse(id)).thenReturn(studentCourse);
    when(repository.searchCourseStatus(studentCourse.getId())).thenReturn(courseStatus);

    // 実行
    CourseDetail result = sut.searchStudentCourse(id);

    // 検証
    verify(repository, times(1)).searchStudentCourse(id);
    verify(repository, times(1)).searchCourseStatus(studentCourse.getId());
    assertNotNull(result);
    assertEquals(studentCourse, result.getStudentCourse());
    assertEquals(courseStatus, result.getCourseStatus());
    assertEquals(result.getStudentCourse().getId(), result.getCourseStatus().getCourseId());

  }

  @Test
  void 受講生コース詳細の検索_異常系_存在しない受講生コースIDをメソッドに渡した場合に例外がスローされること() {
    // 事前準備
    int id = 777;
    StudentCourse studentCourse = new StudentCourse();
    when(repository.searchStudentCourse(id)).thenReturn(null);

    assertThrows(ResourceNotFoundException.class, () -> sut.searchStudentCourse(id));

    verify(repository, times(1)).searchStudentCourse(id);
    verify(repository, never()).searchCourseStatus(studentCourse.getId());

  }

  @Test
  void 受講生詳細情報の新規登録_正常系_リポジトリの処理を適切に呼び出したうえで受講生コース情報の初期情報が登録されコース申込状況が適切にインスタンス化されていること() {
    // 事前準備
    int studentId = 0;
    int courseId1 = 1;
    int courseId2 = 2;

    Student student = new Student();
    student.setId(studentId);

    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setId(courseId1);
    studentCourse2.setId(courseId2);
    studentCourses.add(studentCourse1);
    studentCourses.add(studentCourse2);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    CourseStatus courseStatus1 = new CourseStatus();
    CourseStatus courseStatus2 = new CourseStatus();
    courseStatus1.setCourseId(courseId1);
    courseStatus2.setCourseId(courseId2);

    doNothing().when(repository).registerStudent(any(Student.class));
    doNothing().when(repository).registerStudentCourses(any(StudentCourse.class));
    doNothing().when(repository).registerCourseStatus(any(CourseStatus.class));

    when(repository.searchCourseStatus(studentCourse1.getId())).thenReturn(courseStatus1);
    when(repository.searchCourseStatus(studentCourse2.getId())).thenReturn(courseStatus2);

    LocalDateTime testStartTime = LocalDateTime.now();

    // 実行
    IntegratedDetail result = sut.registerStudent(studentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(2)).registerStudentCourses(any(StudentCourse.class));
    verify(repository, times(2)).registerCourseStatus(any(CourseStatus.class));
    assertNotNull(result);
    assertEquals(student, result.getStudentDetail().getStudent());
    assertEquals(2, result.getStudentDetail().getStudentCourses().size());
    assertEquals(2, result.getCourseDetails().size());

    for (StudentCourse studentCourse : result.getStudentDetail().getStudentCourses()) {
      assertEquals(studentId, studentCourse.getStudentId());
      assertTrue(
          studentCourse.getStartDate().isAfter(testStartTime) || studentCourse.getStartDate()
              .isEqual(testStartTime));
      assertTrue(
          studentCourse.getEndDate().isAfter(studentCourse.getStartDate()));
      assertEquals(1,
          ChronoUnit.YEARS.between(studentCourse.getStartDate(), studentCourse.getEndDate()));

    }

    for (CourseDetail courseDetail : result.getCourseDetails()) {
      assertEquals(courseDetail.getStudentCourse().getId(),
          courseDetail.getCourseStatus().getCourseId());
    }

  }

  @Test
  void 受講生詳細情報の新規登録_異常系_存在するメールアドレスを登録しようとしたときに例外をスローすること() {
    // 事前準備:新規登録する受講生情報の作成
    Student student = new Student();
    student.setMail("test@example.com");

    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourses = new ArrayList<>();
    studentCourses.add(studentCourse);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    // 事前準備:DB上の受講生情報の作成
    Student studentAtDb = new Student();
    studentAtDb.setMail("test@example.com");
    List<Student> studentsAtDb = new ArrayList<>();
    studentsAtDb.add(studentAtDb);

    when(repository.searchStudents()).thenReturn(studentsAtDb);

    // 実行と検証
    assertThrows(EmailAlreadyExistsException.class, () -> sut.registerStudent(studentDetail));

    // 検証
    verify(repository, never()).registerStudent(student);
    verify(repository, never()).registerStudentCourses(any(StudentCourse.class));
    verify(repository, never()).registerCourseStatus(any(CourseStatus.class));

  }

  @Test
  void 受講生詳細情報の更新_正常系_存在する受講生IDを指定したときにリポジトリの処理を適切に呼び出していること() {
    // 事前準備
    Student student = new Student();

    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourses = new ArrayList<>();
    studentCourses.add(studentCourse);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    when(repository.searchStudent(anyInt())).thenReturn(student);
    when(repository.searchStudentCourse(anyInt())).thenReturn(studentCourse);

    doNothing().when(repository).updateStudent(any(Student.class));
    doNothing().when(repository).updateStudentCourses(any(StudentCourse.class));

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).searchStudent(anyInt());
    verify(repository, times(studentCourses.size())).searchStudentCourse(anyInt());
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(studentCourses.size())).updateStudentCourses(studentCourse);

  }

  @Test
  void 受講生詳細情報の更新_異常系_存在しない受講生IDを指定したときに例外をスローすること() {
    // 事前準備
    Student student = new Student();

    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourses = new ArrayList<>();
    studentCourses.add(studentCourse);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    when(repository.searchStudent(anyInt())).thenReturn(null);

    // 実行と検証
    assertThrows(ResourceNotFoundException.class, () -> sut.updateStudent(studentDetail));

    // 検証
    verify(repository, times(1)).searchStudent(anyInt());
    verify(repository, never()).searchStudentCourse(anyInt());
    verify(repository, never()).updateStudent(student);
    verify(repository, never()).updateStudentCourses(studentCourse);

  }

  @Test
  void 受講生詳細情報の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること() {
    // 事前準備
    Student student = new Student();

    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourses = new ArrayList<>();
    studentCourses.add(studentCourse);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    when(repository.searchStudent(anyInt())).thenReturn(student);
    when(repository.searchStudentCourse(anyInt())).thenReturn(null);

    // 実行と検証
    assertThrows(ResourceNotFoundException.class, () -> sut.updateStudent(studentDetail));

    // 検証
    verify(repository, times(1)).searchStudent(anyInt());
    verify(repository, times(studentCourses.size())).searchStudentCourse(anyInt());
    verify(repository, never()).updateStudent(student);
    verify(repository, never()).updateStudentCourses(studentCourse);

  }

  @Test
  void コース申込状況の更新_正常系_存在する受講生コースIDを指定したときにリポジトリの処理を適切に呼び出していること() {
    // 事前準備
    StudentCourse studentCourse = new StudentCourse();
    CourseStatus courseStatus = new CourseStatus();

    when(repository.searchStudentCourse(courseStatus.getCourseId())).thenReturn(studentCourse);
    doNothing().when(repository).updateCourseStatus(any(CourseStatus.class));

    // 実行
    sut.updateCourseStatus(courseStatus);

    // 検証
    verify(repository, times(1)).searchStudentCourse(courseStatus.getCourseId());
    verify(repository, times(1)).updateCourseStatus(courseStatus);

  }

  @Test
  void コース申込状況の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること() {
    // 事前準備
    CourseStatus courseStatus = new CourseStatus();

    when(repository.searchStudentCourse(courseStatus.getCourseId())).thenReturn(null);

    // 実行と検証
    assertThrows(ResourceNotFoundException.class, () -> sut.updateCourseStatus(courseStatus));

    // 検証
    verify(repository, times(1)).searchStudentCourse(courseStatus.getCourseId());
    verify(repository, never()).updateCourseStatus(courseStatus);

  }

}
