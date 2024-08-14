package raisetech.student.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static raisetech.student.management.model.data.Gender.その他;
import static raisetech.student.management.model.data.Gender.男性;
import static raisetech.student.management.model.data.Status.仮申込;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.data.CourseSearchCriteria;
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
import raisetech.student.management.model.services.StudentService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
public class StudentServiceRepositoryIntegrationTest {

  // 結合テストのため、StudentServiceにDIされているConverter、RepositoryのMock化はしない
  @Autowired
  StudentService sut;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @ParameterizedTest
  @MethodSource("provideStudentTestCases")
  void 受講生詳細の一覧検索_引数に応じてフィルタリングされた検索結果が返ってくること(
      StudentSearchCriteria criteria, int expectedResultCount) {
    // 実行
    List<StudentDetail> actualStudentDetails = sut.searchStudentList(criteria);

    // 検証
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
            null, null, null, null), 5),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new StudentSearchCriteria("山田太郎", "ヤマダタロウ",
            "たろう", "taro.yamada@example.com", "東京都", 10, 30,
            Gender.男性, false, "Java",
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 5, 1),
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 2)), 1),
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
  void 受講生コース詳細の一覧検索_引数に応じてフィルタリングされた検索結果が返ってくること(
      CourseSearchCriteria criteria, int expectedResultCount) {
    // 実行
    List<CourseDetail> actualCourseDetails = sut.searchStudentCourseList(criteria);

    // 検証
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
            null, null, null, null, null), 8),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new CourseSearchCriteria("Java",
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 5, 1),
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 2), 仮申込), 1),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つも存在しないケース。
        Arguments.of(new CourseSearchCriteria("Java",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 1), Status.受講終了), 0));

  }

  @Test
  void 受講生詳細の検索_正常系_受講生IDに紐づく受講生情報と受講生コース情報が返ってくること() {
    // 実行
    StudentDetail actual = sut.searchStudent(1);

    // 検証
    assertNotNull(actual);

    assertEquals("山田太郎", actual.getStudent().getFullname());
    assertEquals("ヤマダタロウ", actual.getStudent().getFurigana());
    assertEquals("たろう", actual.getStudent().getNickname());
    assertEquals("taro.yamada@example.com", actual.getStudent().getMail());
    assertEquals("東京都", actual.getStudent().getAddress());
    assertEquals(20, actual.getStudent().getAge());
    assertEquals(男性, actual.getStudent().getGender());
    assertFalse(actual.getStudent().isDeleted());
    assertNull(actual.getStudent().getRemark());

    assertEquals(2, actual.getStudentCourses().size());
    assertEquals(1, actual.getStudentCourses().get(0).getStudentId());
    assertEquals("Java", actual.getStudentCourses().get(0).getCourseName());
    assertEquals(LocalDateTime.of(2024, 4, 1, 9, 0, 0),
        actual.getStudentCourses().get(0).getStartDate());
    assertEquals(LocalDateTime.of(2024, 7, 31, 17, 0, 0),
        actual.getStudentCourses().get(0).getEndDate());
    assertEquals(1, actual.getStudentCourses().get(1).getStudentId());
    assertEquals("Ruby", actual.getStudentCourses().get(1).getCourseName());
    assertEquals(LocalDateTime.of(2024, 4, 2, 13, 0, 0),
        actual.getStudentCourses().get(1).getStartDate());
    assertEquals(LocalDateTime.of(2024, 8, 1, 15, 0, 0),
        actual.getStudentCourses().get(1).getEndDate());

  }

  @Test
  void 受講生詳細の検索_異常系_存在しない受講生IDをメソッドに渡した場合に例外がスローされること() {
    // 実行と検証
    assertThrows(ResourceNotFoundException.class, () -> sut.searchStudent(999));

  }

  @Test
  void 受講生コース詳細の検索_正常系_受講生コースIDに紐づくコース申込状況と受講生コース情報が返ってくること() {

    // 実行
    CourseDetail actual = sut.searchStudentCourse(1);

    // 検証
    assertNotNull(actual);

    assertEquals(1, actual.getStudentCourse().getStudentId());
    assertEquals("Java", actual.getStudentCourse().getCourseName());
    assertEquals(LocalDateTime.of(2024, 4, 1, 9, 0, 0),
        actual.getStudentCourse().getStartDate());
    assertEquals(LocalDateTime.of(2024, 7, 31, 17, 0, 0),
        actual.getStudentCourse().getEndDate());

    assertEquals(1, actual.getCourseStatus().getCourseId());
    assertEquals(仮申込, actual.getCourseStatus().getStatus());

  }

  @Test
  void 受講生コース詳細の検索_異常系_存在しない受講生コースIDをメソッドに渡した場合に例外がスローされること() {
    // 実行と検証
    assertThrows(ResourceNotFoundException.class, () -> sut.searchStudentCourse(999));

  }

  @Test
  void 受講生詳細情報の新規登録_正常系_受講生情報および受講生コース情報およびコース申込状況が登録されること() {
    // 事前準備
    Student student = new Student();
    student.setFullname("追加一郎");
    student.setFurigana("ツイカイチロウ");
    student.setNickname("イチ");
    student.setMail("tuika@example.com");
    student.setAddress("日本");
    student.setAge(23);
    student.setGender(その他);
    student.setRemark("サービス・リポジトリの結合テスト：新規登録の確認");

    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setCourseName("C++");
    studentCourse2.setCourseName("C#");
    studentCourses.add(studentCourse1);
    studentCourses.add(studentCourse2);

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    LocalDateTime testStartTime = LocalDateTime.now();

    // 実行
    IntegratedDetail actual = sut.registerStudent(studentDetail);

    // 検証
    // studentが正しく登録されているか
    Map<String, Object> registerStudentMap = jdbcTemplate.queryForMap(
        "SELECT * FROM students WHERE id=?", actual.getStudentDetail().getStudent().getId());
    assertEquals(6, registerStudentMap.get("id"));
    assertEquals("追加一郎", registerStudentMap.get("fullname"));
    assertEquals("ツイカイチロウ", registerStudentMap.get("furigana"));
    assertEquals("イチ", registerStudentMap.get("nickname"));
    assertEquals("tuika@example.com", registerStudentMap.get("mail"));
    assertEquals("日本", registerStudentMap.get("address"));
    assertEquals(23, registerStudentMap.get("age"));
    assertEquals("その他", registerStudentMap.get("gender"));
    assertEquals(false, registerStudentMap.get("deleted"));
    assertEquals("サービス・リポジトリの結合テスト：新規登録の確認",
        registerStudentMap.get("remark"));

    // studentCourse1が正しく登録されているか
    Map<String, Object> registerStudentCourse1Map = jdbcTemplate.queryForMap(
        "SELECT * FROM students_courses WHERE id=?",
        actual.getStudentDetail().getStudentCourses().get(0).getId());
    assertEquals(9, registerStudentCourse1Map.get("id"));
    assertEquals(6, registerStudentCourse1Map.get("student_id"));
    assertEquals("C++", registerStudentCourse1Map.get("course_name"));
    LocalDate expectedStartDate1 = testStartTime.toLocalDate();
    LocalDate actualStartDate1 = ((Timestamp) registerStudentCourse1Map.get(
        "start_date")).toLocalDateTime().toLocalDate();
    assertEquals(expectedStartDate1, actualStartDate1, "年月日のみ合っていればOKとする");
    LocalDate expectedEndDate1 = testStartTime.plusYears(1).toLocalDate();
    LocalDate actualEndDate1 = ((Timestamp) registerStudentCourse1Map.get(
        "end_date")).toLocalDateTime().toLocalDate();
    assertEquals(expectedEndDate1, actualEndDate1, "年月日のみ合っていればOKとする");

    // studentCourse2が正しく登録されているか
    Map<String, Object> registerStudentCourse2Map = jdbcTemplate.queryForMap(
        "SELECT * FROM students_courses WHERE id=?",
        actual.getStudentDetail().getStudentCourses().get(1).getId());
    assertEquals(10, registerStudentCourse2Map.get("id"));
    assertEquals(6, registerStudentCourse2Map.get("student_id"));
    assertEquals("C#", registerStudentCourse2Map.get("course_name"));
    LocalDate expectedStartDate2 = testStartTime.toLocalDate();
    LocalDate actualStartDate2 = ((Timestamp) registerStudentCourse2Map.get(
        "start_date")).toLocalDateTime().toLocalDate();
    assertEquals(expectedStartDate2, actualStartDate2, "年月日のみ合っていればOKとする");
    LocalDate expectedEndDate2 = testStartTime.plusYears(1).toLocalDate();
    LocalDate actualEndDate2 = ((Timestamp) registerStudentCourse2Map.get(
        "end_date")).toLocalDateTime().toLocalDate();
    assertEquals(expectedEndDate2, actualEndDate2, "年月日のみ合っていればOKとする");

    // CourseStatus1が正しく登録されているか
    Map<String, Object> registerCourseStatus1Map = jdbcTemplate.queryForMap(
        "SELECT * FROM course_status WHERE id=?",
        actual.getCourseDetails().get(0).getCourseStatus().getId());
    assertEquals(9, registerCourseStatus1Map.get("id"));
    assertEquals(9, registerCourseStatus1Map.get("course_id"));
    assertEquals("仮申込", registerCourseStatus1Map.get("status"));

    // CourseStatus2が正しく登録されているか
    Map<String, Object> registerCourseStatus2Map = jdbcTemplate.queryForMap(
        "SELECT * FROM course_status WHERE id=?",
        actual.getCourseDetails().get(1).getCourseStatus().getId());
    assertEquals(10, registerCourseStatus2Map.get("id"));
    assertEquals(10, registerCourseStatus2Map.get("course_id"));
    assertEquals("仮申込", registerCourseStatus2Map.get("status"));

  }

  @Test
  void 受講生詳細情報の新規登録_異常系_存在するメールアドレスを登録しようとしたときに例外をスローすること() {
    // 事前準備
    Student student = new Student();
    student.setMail("taro.yamada@example.com"); // data.sqlですでに登録されているメールアドレス

    List<StudentCourse> studentCourses = new ArrayList<>();

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);

    // 実行と検証
    assertThrows(EmailAlreadyExistsException.class, () -> sut.registerStudent(studentDetail));

  }

}
