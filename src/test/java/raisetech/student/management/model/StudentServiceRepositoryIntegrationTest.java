package raisetech.student.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static raisetech.student.management.model.data.Gender.男性;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.Gender;
import raisetech.student.management.model.data.Status;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.services.StudentService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
public class StudentServiceRepositoryIntegrationTest {

  // 結合テストのため、StudentServiceにDIされているConverter、RepositoryのMock化はしない
  @Autowired
  StudentService sut;

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
            LocalDate.of(2024, 8, 2), Status.仮申込), 1),
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

}
