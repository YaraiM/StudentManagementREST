package raisetech.student.management;

import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raisetech.student.management.model.data.Status.仮申込;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.Gender;
import raisetech.student.management.model.data.StudentSearchCriteria;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentControllerServiceRepositoryIntegrationTest {

  @Autowired
  MockMvc mockMvc; //Spring MVCのエンドポイントをテストするためのモックオブジェクト

  @Autowired
  private ObjectMapper objectMapper; // オブジェクトをJSON文字列に変換するためのオブジェクト

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @MethodSource("provideStudentTestCases")
  void 受講生詳細の一覧検索_リクエストパラメータに応じて検索結果を取得できること(
      StudentSearchCriteria criteria, List<Integer> expectedStudentIds,
      List<String> expectedFullnames,
      List<String> expectedFuriganas, List<String> expectedNicknames, List<String> expectedMails,
      List<String> expectedAddresses, List<Integer> expectedAges, List<String> expectedGenders,
      List<Boolean> expectedIsDeleted, List<String> expectedRemarks,
      List<Integer> expectedCourseIds, List<Integer> expectedStudentIdsInCourses,
      List<String> expectedCourseNames,
      List<String> expectedStartDates, List<String> expectedEndDates)
      throws Exception {
    // 実行と検証
    // HttpリクエストのクエリパラメータはすべてStringで送信されるため、String以外を文字列変換したときにはパラメータとして指定されないようにしなければならない（nullチェック）
    mockMvc.perform(get("/students")
            .param("fullname", criteria.getFullname())
            .param("furigana", criteria.getFurigana())
            .param("nickname", criteria.getNickname())
            .param("mail", criteria.getMail())
            .param("address", criteria.getAddress())
            .param("minAge",
                criteria.getMinAge() != null ? String.valueOf(criteria.getMinAge()) : null)
            .param("maxAge",
                criteria.getMaxAge() != null ? String.valueOf(criteria.getMaxAge()) : null)
            .param("gender", criteria.getGender() != null ? criteria.getGender().toString() : null)
            .param("deleted",
                criteria.getDeleted() != null ? String.valueOf(criteria.getDeleted()) : null)
            .param("courseName", criteria.getCourseName())
            .param("startDateFrom",
                criteria.getStartDateFrom() != null ? criteria.getStartDateFrom().toString() : null)
            .param("startDateTo",
                criteria.getStartDateTo() != null ? criteria.getStartDateTo().toString() : null)
            .param("endDateFrom",
                criteria.getEndDateFrom() != null ? criteria.getEndDateFrom().toString() : null)
            .param("endDateTo",
                criteria.getEndDateTo() != null ? criteria.getEndDateTo().toString() : null)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(expectedStudentIds.size())) // valueは単一の値を検証する
        // 以下、複数要素を順序込みで比較する。比較対象がJSON「配列」のため、expectedIdsをtoArrayしないと例外が発生する。
        .andExpect(jsonPath("$[*].student.id", contains(expectedStudentIds.toArray())))
        .andExpect(jsonPath("$[*].student.fullname", contains(expectedFullnames.toArray())))
        .andExpect(jsonPath("$[*].student.furigana", contains(expectedFuriganas.toArray())))
        .andExpect(jsonPath("$[*].student.nickname", contains(expectedNicknames.toArray())))
        .andExpect(jsonPath("$[*].student.mail", contains(expectedMails.toArray())))
        .andExpect(jsonPath("$[*].student.address", contains(expectedAddresses.toArray())))
        .andExpect(jsonPath("$[*].student.age", contains(expectedAges.toArray())))
        .andExpect(jsonPath("$[*].student.gender", contains(expectedGenders.toArray())))
        .andExpect(jsonPath("$[*].student.deleted", contains(expectedIsDeleted.toArray())))
        .andExpect(jsonPath("$[*].student.remark", contains(expectedRemarks.toArray())))
        .andExpect(jsonPath("$[*].studentCourses[*].id", contains(expectedCourseIds.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourses[*].studentId",
                contains(expectedStudentIdsInCourses.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourses[*].courseName", contains(expectedCourseNames.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourses[*].startDate", contains(expectedStartDates.toArray())))
        .andExpect(jsonPath("$[*].studentCourses[*].endDate", contains(expectedEndDates.toArray())))

        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);

          // JSONPathのIdsの結果を直接確認
          DocumentContext context = JsonPath.parse(content);
          List<Integer> ids = context.read("$[*].student.id");
          System.out.println("Extracted IDs: " + ids);

          // 期待値を出力して直接目視確認する
          System.out.println("Expected IDs: " + expectedStudentIds);
        });

  }

  /**
   * 受講生詳細一覧検索のパラメータテストに適用するテストケースです。
   *
   * @return Argument
   */
  private static Stream<Arguments> provideStudentTestCases() {
    return Stream.of(
        // リクエストパラメータなし。全件検索が行われるケース。
        Arguments.of(
            new StudentSearchCriteria(null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null),
            Arrays.asList(1, 2, 3, 4, 5),
            Arrays.asList("山田太郎", "佐藤花子", "鈴木一郎", "田中美咲", "中村健太"),
            Arrays.asList("ヤマダタロウ", "サトウハナコ", "スズキイチロウ", "タナカミサキ",
                "ナカムラケンタ"),
            Arrays.asList("たろう", "はなちゃん", null, "みさき", "けん"),
            Arrays.asList("taro.yamada@example.com", "hanako.sato@example.com",
                "ichiro.suzuki@example.com", "misaki.tanaka@example.com",
                "kenta.nakamura@example.com"),
            Arrays.asList("東京都", "大阪府", "愛知県", "福岡県", "北海道"),
            Arrays.asList(20, 32, 41, 59, 19),
            Arrays.asList("男性", "女性", "男性", "女性", "その他"),
            Arrays.asList(false, false, false, false, true),
            Arrays.asList(null, null, "なごや", "博多弁", "留学経験あり"),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Arrays.asList(1, 1, 2, 2, 3, 3, 4, 5),
            Arrays.asList("Java", "Ruby", "Design", "Front", "Python", "Java", "English", "AWS"),
            Arrays.asList("2024-04-01T09:00:00", "2024-04-02T13:00:00", "2024-04-01T10:30:00",
                "2024-04-03T14:00:00", "2024-04-02T09:00:00", "2024-04-04T13:30:00",
                "2024-04-01T11:00:00", "2024-04-03T10:00:00"),
            Arrays.asList("2024-07-31T17:00:00", "2024-08-01T15:00:00", "2024-07-31T12:30:00",
                "2024-08-02T16:00:00", "2024-08-01T11:00:00", "2024-08-03T15:30:00",
                "2024-07-31T13:00:00", "2024-08-02T12:00:00")),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new StudentSearchCriteria("山田太郎", "ヤマダタロウ",
                "たろう", "taro.yamada@example.com", "東京都", 10, 30,
                Gender.男性, false, "Java",
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 2)),
            Collections.singletonList(1),
            Collections.singletonList("山田太郎"),
            Collections.singletonList("ヤマダタロウ"),
            Collections.singletonList("たろう"),
            Collections.singletonList("taro.yamada@example.com"),
            Collections.singletonList("東京都"),
            Collections.singletonList(20),
            Collections.singletonList("男性"),
            Collections.singletonList(false),
            Collections.singletonList(null),
            Arrays.asList(1, 2),
            Arrays.asList(1, 1),
            Arrays.asList("Java", "Ruby"),
            Arrays.asList("2024-04-01T09:00:00", "2024-04-02T13:00:00"),
            Arrays.asList("2024-07-31T17:00:00", "2024-08-01T15:00:00")
        )
    );
  }

  @Test
  void 受講生詳細の一覧検索_リクエストパラメータに合致するデータがない場合に検索結果が0件であること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(get("/students")
            .param("fullname", "鈴木太郎")
            .param("furigana", "たなかたろう")
            .param("nickname", "たなっち")
            .param("mail", "tanaka@example.com")
            .param("address", "東京")
            .param("minAge", "10")
            .param("maxAge", "30")
            .param("gender", "男性")
            .param("deleted", "false")
            .param("courseName", "Java")
            .param("startDateFrom", LocalDate.of(2024, 6, 1).toString())
            .param("startDateTo", LocalDate.of(2024, 8, 1).toString())
            .param("endDateFrom", LocalDate.of(2025, 6, 1).toString())
            .param("endDateTo", LocalDate.of(2025, 8, 1).toString())
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0))
        .andExpect(jsonPath("$").isEmpty());

  }

  @ParameterizedTest
  @MethodSource("provideCourseTestCases")
  void 受講生コース詳細の一覧検索_リクエストパラメータに応じて検索結果を取得できること(
      CourseSearchCriteria criteria, List<Integer> expectedCourseIds,
      List<Integer> expectedStudentIdsInCourse, List<String> expectedCourseNames,
      List<String> expectedStartDates, List<String> expectedEndDates,
      List<Integer> expectedStatusIds, List<Integer> expectedCourseIdsInStatus,
      List<String> expectedStatus)
      throws Exception {
    mockMvc.perform(get("/students/courses")
            .param("courseName", criteria.getCourseName())
            .param("startDateFrom",
                criteria.getStartDateFrom() != null ? criteria.getStartDateFrom().toString() : null)
            .param("startDateTo",
                criteria.getStartDateTo() != null ? criteria.getStartDateTo().toString() : null)
            .param("endDateFrom",
                criteria.getEndDateFrom() != null ? criteria.getEndDateFrom().toString() : null)
            .param("endDateTo",
                criteria.getEndDateTo() != null ? criteria.getEndDateTo().toString() : null)
            .param("status", criteria.getStatus() != null ? criteria.getStatus().toString() : null)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(expectedCourseIds.size()))
        .andExpect(jsonPath("$[*].studentCourse.id", contains(expectedCourseIds.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourse.studentId",
                contains(expectedStudentIdsInCourse.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourse.courseName", contains(expectedCourseNames.toArray())))
        .andExpect(
            jsonPath("$[*].studentCourse.startDate", contains(expectedStartDates.toArray())))
        .andExpect(jsonPath("$[*].studentCourse.endDate", contains(expectedEndDates.toArray())))
        .andExpect(jsonPath("$[*].studentCourse.id", contains(expectedCourseIds.toArray())))
        .andExpect(jsonPath("$[*].courseStatus.id", contains(expectedStatusIds.toArray())))
        .andExpect(
            jsonPath("$[*].courseStatus.courseId", contains(expectedCourseIdsInStatus.toArray())))
        .andExpect(jsonPath("$[*].courseStatus.status", contains(expectedStatus.toArray())))

        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);

        });
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
                null, null, null, null, null),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Arrays.asList(1, 1, 2, 2, 3, 3, 4, 5),
            Arrays.asList("Java", "Ruby", "Design", "Front", "Python", "Java", "English", "AWS"),
            Arrays.asList("2024-04-01T09:00:00", "2024-04-02T13:00:00", "2024-04-01T10:30:00",
                "2024-04-03T14:00:00", "2024-04-02T09:00:00", "2024-04-04T13:30:00",
                "2024-04-01T11:00:00", "2024-04-03T10:00:00"),
            Arrays.asList("2024-07-31T17:00:00", "2024-08-01T15:00:00", "2024-07-31T12:30:00",
                "2024-08-02T16:00:00", "2024-08-01T11:00:00", "2024-08-03T15:30:00",
                "2024-07-31T13:00:00", "2024-08-02T12:00:00"),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Arrays.asList("仮申込", "本申込", "受講中", "受講終了", "仮申込", "本申込", "受講中",
                "受講終了")),
        // すべてのリクエストパラメータを入力し、条件に合致するものが一つだけ存在するケース。
        Arguments.of(new CourseSearchCriteria("Java",
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 2), 仮申込),
            Collections.singletonList(1),
            Collections.singletonList(1),
            Collections.singletonList("Java"),
            Collections.singletonList("2024-04-01T09:00:00"),
            Collections.singletonList("2024-07-31T17:00:00"),
            Collections.singletonList(1),
            Collections.singletonList(1),
            Collections.singletonList("仮申込")
        ));

  }

  @Test
  void 受講生コース詳細の一覧検索_リクエストパラメータに合致するデータがない場合に検索結果が0件であること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(get("/students/courses")
            .param("courseName", "Java")
            .param("startDateFrom", LocalDate.of(2024, 6, 1).toString())
            .param("startDateTo", LocalDate.of(2024, 8, 1).toString())
            .param("endDateFrom", LocalDate.of(2025, 6, 1).toString())
            .param("endDateTo", LocalDate.of(2025, 8, 1).toString())
            .param("status", "受講終了")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0))
        .andExpect(jsonPath("$").isEmpty());

  }

  @Test
  void 受講生詳細の検索_正常系_指定した受講生IDに合致したstudentDetailが返ってくること()
      throws Exception {

    // 実行と検証
    mockMvc.perform(get("/students/detail")
            .param("id", String.valueOf(1))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.id").value(1))
        .andExpect(jsonPath("$.student.fullname").value("山田太郎"))
        .andExpect(jsonPath("$.student.furigana").value("ヤマダタロウ"))
        .andExpect(jsonPath("$.student.nickname").value("たろう"))
        .andExpect(jsonPath("$.student.mail").value("taro.yamada@example.com"))
        .andExpect(jsonPath("$.student.address").value("東京都"))
        .andExpect(jsonPath("$.student.age").value(20))
        .andExpect(jsonPath("$.student.gender").value("男性"))
        .andExpect(jsonPath("$.student.deleted").value(false))
        .andExpect(jsonPath("$.student.remark").isEmpty())
        .andExpect(jsonPath("$.studentCourses[0].id").value(1))
        .andExpect(jsonPath("$.studentCourses[0].studentId").value(1))
        .andExpect(jsonPath("$.studentCourses[0].courseName").value("Java"))
        .andExpect(jsonPath("$.studentCourses[0].startDate").value("2024-04-01T09:00:00"))
        .andExpect(jsonPath("$.studentCourses[0].endDate").value("2024-07-31T17:00:00"))
        .andExpect(jsonPath("$.studentCourses[1].id").value(2))
        .andExpect(jsonPath("$.studentCourses[1].studentId").value(1))
        .andExpect(jsonPath("$.studentCourses[1].courseName").value("Ruby"))
        .andExpect(jsonPath("$.studentCourses[1].startDate").value("2024-04-02T13:00:00"))
        .andExpect(jsonPath("$.studentCourses[1].endDate").value("2024-08-01T15:00:00"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);

        });

  }

  @Test
  void 受講生詳細の検索_異常系_存在しない受講生IDを指定したときに例外がスローされること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(get("/students/detail")
            .param("id", String.valueOf(999)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("受講生ID 「" + 999 + "」は存在しません"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);

        });
  }

  @Test
  void 受講生コース詳細の検索_正常系_指定した受講生コースIDに合致したcourseDetailが返ってくること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(get("/students/courses/detail")
            .param("id", String.valueOf(1)))
        .andExpect(jsonPath("$.studentCourse.id").value(1))
        .andExpect(jsonPath("$.studentCourse.studentId").value(1))
        .andExpect(jsonPath("$.studentCourse.courseName").value("Java"))
        .andExpect(jsonPath("$.studentCourse.startDate").value("2024-04-01T09:00:00"))
        .andExpect(jsonPath("$.studentCourse.endDate").value("2024-07-31T17:00:00"))
        .andExpect(jsonPath("$.courseStatus.id").value(1))
        .andExpect(jsonPath("$.courseStatus.courseId").value(1))
        .andExpect(jsonPath("$.courseStatus.status").value("仮申込"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);
        });
  }

  @Test
  void 受講生コース詳細の検索_異常系_存在しない受講生IDを指定したときに例外がスローされること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(get("/students/courses/detail")
            .param("id", String.valueOf(999)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("受講生コースID 「" + 999 + "」は存在しません"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);
        });

  }

  @Test
  void 受講生の新規登録_正常系_JSON形式のリクエストボディを指定して新規登録できること()
      throws Exception {
    // 事前準備
    LocalDate testStartDate = LocalDate.now();
    // 実行と検証
    mockMvc.perform(post("/students/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {
                        "student": {
                            "fullname": "田中昭三",
                            "furigana": "たなかしょうぞう",
                            "nickname": "ショーゾー",
                            "mail": "shozo@example.com",
                            "address": "東京",
                            "age": 55,
                            "gender": "男性",
                            "remark": "新規登録のテストです"
                        },
                        "studentCourses": [
                            {
                                "courseName": "Java"
                            },
                            {
                                "courseName": "Ruby"
                            }
                        ]
                    }
                    """
            )
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        // studentの検証
        .andExpect(jsonPath("$.studentDetail.student.id").value(6))
        .andExpect(jsonPath("$.studentDetail.student.fullname").value("田中昭三"))
        .andExpect(jsonPath("$.studentDetail.student.furigana").value("たなかしょうぞう"))
        .andExpect(jsonPath("$.studentDetail.student.nickname").value("ショーゾー"))
        .andExpect(jsonPath("$.studentDetail.student.mail").value("shozo@example.com"))
        .andExpect(jsonPath("$.studentDetail.student.address").value("東京"))
        .andExpect(jsonPath("$.studentDetail.student.age").value(55))
        .andExpect(jsonPath("$.studentDetail.student.gender").value("男性"))
        .andExpect(jsonPath("$.studentDetail.student.deleted").value(false))
        .andExpect(jsonPath("$.studentDetail.student.remark").value("新規登録のテストです"))
        //studentDetail下のstudentCoursesの検証
        .andExpect(jsonPath("$.studentDetail.studentCourses[0].id").value(9))
        .andExpect(jsonPath("$.studentDetail.studentCourses[0].studentId").value(6))
        .andExpect(jsonPath("$.studentDetail.studentCourses[0].courseName").value("Java"))
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualStartDateStr = jsonNode.path("studentDetail")
              .path("studentCourses")
              .get(0)
              .path("startDate")
              .asText();
          LocalDate actualStartDate = LocalDate.parse(actualStartDateStr.split("T")[0]);
          assertEquals(testStartDate, actualStartDate, "年月日のみ合っていればOKとする");
        })
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualEndDateStr = jsonNode.path("studentDetail")
              .path("studentCourses")
              .get(0)
              .path("endDate")
              .asText();
          LocalDate actualEndDate = LocalDate.parse(actualEndDateStr.split("T")[0]);
          assertEquals(testStartDate.plusYears(1), actualEndDate,
              "年月日のみ合っていればOKとする");
        })
        .andExpect(jsonPath("$.studentDetail.studentCourses[1].id").value(10))
        .andExpect(jsonPath("$.studentDetail.studentCourses[1].studentId").value(6))
        .andExpect(jsonPath("$.studentDetail.studentCourses[1].courseName").value("Ruby"))
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualStartDateStr = jsonNode.path("studentDetail")
              .path("studentCourses")
              .get(1)
              .path("startDate")
              .asText();
          LocalDate actualStartDate = LocalDate.parse(actualStartDateStr.split("T")[0]);
          assertEquals(testStartDate, actualStartDate, "年月日のみ合っていればOKとする");
        })
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualEndDateStr = jsonNode.path("studentDetail")
              .path("studentCourses")
              .get(1)
              .path("endDate")
              .asText();
          LocalDate actualEndDate = LocalDate.parse(actualEndDateStr.split("T")[0]);
          assertEquals(testStartDate.plusYears(1), actualEndDate,
              "年月日のみ合っていればOKとする");
        })
        //CourseDetails下のstudentCourseの検証
        .andExpect(jsonPath("$.courseDetails[0].studentCourse.id").value(9))
        .andExpect(jsonPath("$.courseDetails[0].studentCourse.studentId").value(6))
        .andExpect(jsonPath("$.courseDetails[0].studentCourse.courseName").value("Java"))
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualStartDateStr = jsonNode
              .path("courseDetails")
              .get(0)
              .path("studentCourse")
              .path("startDate")
              .asText();
          LocalDate actualStartDate = LocalDate.parse(actualStartDateStr.split("T")[0]);
          assertEquals(testStartDate, actualStartDate, "年月日のみ合っていればOKとする");
        })
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualEndDateStr = jsonNode
              .path("courseDetails")
              .get(0)
              .path("studentCourse")
              .path("endDate")
              .asText();
          LocalDate actualEndDate = LocalDate.parse(actualEndDateStr.split("T")[0]);
          assertEquals(testStartDate.plusYears(1), actualEndDate,
              "年月日のみ合っていればOKとする");
        })
        .andExpect(jsonPath("$.courseDetails[1].studentCourse.id").value(10))
        .andExpect(jsonPath("$.courseDetails[1].studentCourse.studentId").value(6))
        .andExpect(jsonPath("$.courseDetails[1].studentCourse.courseName").value("Ruby"))
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualStartDateStr = jsonNode
              .path("courseDetails")
              .get(1)
              .path("studentCourse")
              .path("startDate")
              .asText();
          LocalDate actualStartDate = LocalDate.parse(actualStartDateStr.split("T")[0]);
          assertEquals(testStartDate, actualStartDate, "年月日のみ合っていればOKとする");
        })
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          JsonNode jsonNode = objectMapper.readTree(responseContent);
          String actualEndDateStr = jsonNode
              .path("courseDetails")
              .get(1)
              .path("studentCourse")
              .path("endDate")
              .asText();
          LocalDate actualEndDate = LocalDate.parse(actualEndDateStr.split("T")[0]);
          assertEquals(testStartDate.plusYears(1), actualEndDate,
              "年月日のみ合っていればOKとする");
        })
        //courseStatusの検証
        .andExpect(jsonPath("$.courseDetails[0].courseStatus.id").value(9))
        .andExpect(jsonPath("$.courseDetails[0].courseStatus.courseId").value(9))
        .andExpect(jsonPath("$.courseDetails[0].courseStatus.status").value("仮申込"))
        .andExpect(jsonPath("$.courseDetails[1].courseStatus.id").value(10))
        .andExpect(jsonPath("$.courseDetails[1].courseStatus.courseId").value(10))
        .andExpect(jsonPath("$.courseDetails[1].courseStatus.status").value("仮申込"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);

        });

  }

  @Test
  void 受講生の新規登録_異常系_すでに登録されているメールアドレスを指定したときに例外がスローされること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(post("/students/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {
                        "student": {
                            "fullname": "田中昭三",
                            "furigana": "たなかしょうぞう",
                            "nickname": "ショーゾー",
                            "mail": "taro.yamada@example.com",
                            "address": "東京",
                            "age": 55,
                            "gender": "男性",
                            "remark": "新規登録のテストです"
                        },
                        "studentCourses": [
                            {
                                "courseName": "Java"
                            },
                            {
                                "courseName": "Ruby"
                            }
                        ]
                    }
                    """
            ))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(
            "メールアドレス(taro.yamada@example.com)はすでに登録されているため使用できません。"))
        // MvcResultからレスポンスを取得し、中身をコンソールに出力する。
        .andExpect(result -> {
          result.getResponse().setCharacterEncoding("UTF-8");
          String content = result.getResponse().getContentAsString();
          System.out.println("Response Content: " + content);
        });
  }

//  @Test
//  void 受講生の更新_正常系_エンドポイントでサービスの処理が適切に呼び出され_更新処理が成功しました_というメッセージが返ってくること()
//      throws Exception {
//
//    // 実行と検証
//    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(
//                """
//                        {
//                            "student": {
//                                "id": 1,
//                                "fullname": "田中昭三",
//                                "furigana": "たなかしょうぞう",
//                                "nickname": "ショーゾー",
//                                "mail": "shozo@example.com",
//                                "address": "東京",
//                                "age": 56,
//                                "gender": "男性",
//                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
//                                "deleted": true
//                            },
//                            "studentCourses": [
//                                {
//                                    "id": 1,
//                                    "student_id": 1,
//                                    "courseName": "Java"
//                                },
//                                {
//                                    "id":2,
//                                    "student_id": 1,
//                                    "courseName": "Ruby"
//                                }
//                            ]
//                        }
//                    """
//            ))
//        .andExpect(status().isOk())
//        .andExpect(content().string("更新処理が成功しました"));
//
//    verify(service, times(1)).updateStudent(any());
//
//  }
//
//  @Test
//  void 受講生の更新_異常系_存在しない受講生IDを指定したときに例外をスローすること()
//      throws Exception {
//    // 事前準備
//    Student student = new Student();
//    student.setId(999);
//    StudentDetail studentDetail = new StudentDetail();
//    studentDetail.setStudent(student);
//
//    String expectedErrorMessage =
//        "受講生ID 「" + studentDetail.getStudent().getId() + "」は存在しません";
//
//    doThrow(new ResourceNotFoundException(expectedErrorMessage))
//        .when(service).updateStudent(any(StudentDetail.class));
//
//    // 実行と検証
//    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(
//                """
//                        {
//                            "student": {
//                                "id": 999,
//                                "fullname": "田中昭三",
//                                "furigana": "たなかしょうぞう",
//                                "nickname": "ショーゾー",
//                                "mail": "shozo@example.com",
//                                "address": "東京",
//                                "age": 56,
//                                "gender": "男性",
//                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
//                                "deleted": true
//                            },
//                            "studentCourses": [
//                                {
//                                    "id": 1,
//                                    "student_id": 999,
//                                    "courseName": "Java"
//                                },
//                                {
//                                    "id":2,
//                                    "student_id": 999,
//                                    "courseName": "Ruby"
//                                }
//                            ]
//                        }
//                    """
//            ))
//        .andExpect(status().isNotFound())
//        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
//
//  }
//
//  @Test
//  void 受講生の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること()
//      throws Exception {
//    // 事前準備
//    StudentCourse studentCourse = new StudentCourse();
//    studentCourse.setId(999);
//    List<StudentCourse> studentCourses = new ArrayList<>();
//    studentCourses.add(studentCourse);
//    StudentDetail studentDetail = new StudentDetail();
//    studentDetail.setStudentCourses(studentCourses);
//
//    String expectedErrorMessage =
//        "受講生コースID 「" + studentDetail.getStudentCourses().getFirst().getId()
//            + "」は存在しません";
//
//    doThrow(new ResourceNotFoundException(expectedErrorMessage))
//        .when(service).updateStudent(any(StudentDetail.class));
//
//    // 実行と検証
//    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(
//                """
//                        {
//                            "student": {
//                                "id": 1,
//                                "fullname": "田中昭三",
//                                "furigana": "たなかしょうぞう",
//                                "nickname": "ショーゾー",
//                                "mail": "shozo@example.com",
//                                "address": "東京",
//                                "age": 56,
//                                "gender": "男性",
//                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
//                                "deleted": true
//                            },
//                            "studentCourses": [
//                                {
//                                    "id": 1,
//                                    "student_id": 999,
//                                    "courseName": "Java"
//                                },
//                                {
//                                    "id":2,
//                                    "student_id": 999,
//                                    "courseName": "Ruby"
//                                }
//                            ]
//                        }
//                    """
//            ))
//        .andExpect(status().isNotFound())
//        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
//
//  }
//
//  @Test
//  void コース申込状況の更新_正常系_エンドポイントでサービスの処理が適切に呼び出され_更新処理が成功しました_というメッセージが返ってくること()
//      throws Exception {
//
//    // 実行と検証
//    mockMvc.perform(MockMvcRequestBuilders.put("/students/courses/statuses/update")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(
//                """
//                    {"courseId":1, "status":"受講中"}
//                    """
//            ))
//        .andExpect(status().isOk())
//        .andExpect(content().string("更新処理が成功しました"));
//
//    verify(service, times(1)).updateCourseStatus(any());
//
//  }
//
//  @Test
//  void コース申込状況の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること()
//      throws Exception {
//    // 事前準備
//    CourseStatus courseStatus = new CourseStatus();
//    courseStatus.setCourseId(999);
//
//    String expectedErrorMessage =
//        "受講生コースID 「" + courseStatus.getCourseId() + "」は存在しません";
//
//    doThrow(new ResourceNotFoundException(expectedErrorMessage))
//        .when(service).updateCourseStatus(any(CourseStatus.class));
//
//    // 実行と検証
//    mockMvc.perform(MockMvcRequestBuilders.put("/students/courses/statuses/update")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(
//                """
//                    {"courseId":999, "status":"受講中"}
//                    """
//            ))
//        .andExpect(status().isNotFound())
//        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
//
//  }
//
//  @Test
//  void 受講生詳細情報の入力チェック_リクエスト可能な情報がすべて適切な場合に入力チェックがかからないこと()
//      throws Exception {
//    // 事前準備
//    int id = 666;
//    StudentDetail studentDetail = createTestStudentDetail(id);
//
//    studentDetail.getStudent().setFullname("田中昭三");
//    studentDetail.getStudent().setFurigana("たなかしょうぞう");
//    studentDetail.getStudent().setNickname("ショーゾー");
//    studentDetail.getStudent().setMail("shozo@example.com");
//    studentDetail.getStudent().setAddress("東京");
//    studentDetail.getStudent().setAge(55);
//    studentDetail.getStudent().setGender(男性);
//    studentDetail.getStudent().setRemark("新規登録のテストです");
//    studentDetail.getStudent().setDeleted(false);
//
//    studentDetail.getStudentCourses().get(0).setCourseName("Java");
//    studentDetail.getStudentCourses().get(1).setCourseName("Ruby");
//
//    Set<ConstraintViolation<StudentDetail>> violations = validator.validate(studentDetail);
//
//    // 検証
//    assertEquals(0, violations.size());
//
//  }
//
//  @Test
//  void 受講生詳細情報の入力チェック_Gender以外のリクエスト可能な情報のうち入力値の指定がある項目が不適切な場合に入力チェックがかかること()
//      throws Exception {
//    // 事前準備
//    int id = 666;
//    StudentDetail studentDetail = createTestStudentDetail(id);
//
//    studentDetail.getStudent().setFullname(""); // fullnameは空白を許容しない
//    studentDetail.getStudent().setFurigana(""); // fuliganaは空白を許容しない
//    studentDetail.getStudent().setNickname("ショーゾー");
//    studentDetail.getStudent().setMail("shozo&example.com"); // mailはメールアドレス以外の入力値を許容しない
//    studentDetail.getStudent().setAddress("東京");
//    studentDetail.getStudent().setAge(55);
//    studentDetail.getStudent().setGender(男性);
//    studentDetail.getStudent().setRemark("新規登録のテストです");
//    studentDetail.getStudent().setDeleted(false);
//
//    studentDetail.getStudentCourses().get(0).setCourseName(""); // courseNameは空白を許容しない
//    studentDetail.getStudentCourses().get(1).setCourseName(""); // courseNameは空白を許容しない
//
//    Set<ConstraintViolation<StudentDetail>> violations = validator.validate(studentDetail);
//
//    // 検証
//    assertEquals(5, violations.size());
//
//    Map<String, String> violationMessages = new HashMap<>();
//    for (ConstraintViolation<StudentDetail> violation : violations) {
//      String propertyPath = violation.getPropertyPath().toString();
//      String message = violation.getMessage();
//      violationMessages.put(propertyPath, message);
//    }
//
//    assertTrue(violationMessages.containsKey("student.fullname"));
//    assertEquals("空白は許可されていません", violationMessages.get("student.fullname"));
//
//    assertTrue(violationMessages.containsKey("student.furigana"));
//    assertEquals("空白は許可されていません", violationMessages.get("student.furigana"));
//
//    assertTrue(violationMessages.containsKey("student.mail"));
//    assertEquals("電子メールアドレスとして正しい形式にしてください",
//        violationMessages.get("student.mail"));
//
//    assertTrue(violationMessages.containsKey("studentCourses[0].courseName"));
//    assertEquals("空白は許可されていません", violationMessages.get("studentCourses[0].courseName"));
//
//    assertTrue(violationMessages.containsKey("studentCourses[1].courseName"));
//    assertEquals("空白は許可されていません", violationMessages.get("studentCourses[1].courseName"));
//
//  }
//
//  @Test
//  void コース申込状況の入力チェック_リクエスト可能な情報がすべて適切な場合に入力チェックがかからないこと()
//      throws Exception {
//    // 事前準備
//    CourseStatus courseStatus = new CourseStatus();
//
//    courseStatus.setId(111);
//    courseStatus.setCourseId(222);
//    courseStatus.setStatus(仮申込);
//
//    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);
//
//    // 検証
//    assertEquals(0, violations.size());
//
//  }
//
//  @Test
//  void コース申込状況の入力チェック_リクエスト可能な情報のうち入力値の指定がある項目が不適切な場合に入力チェックがかかること()
//      throws Exception {
//    // 事前準備
//    CourseStatus courseStatus = new CourseStatus();
//
//    courseStatus.setId(111); // ユーザーがリクエストできない情報のため検証不要
//    courseStatus.setCourseId(222); // ユーザーがリクエストできない情報のため検証不要
//    courseStatus.setStatus(null); // statusはnullを許容しない
//
//    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);
//
//    // 検証
//    assertEquals(1, violations.size());
//
//    Map<String, String> violationMessages = new HashMap<>();
//    for (ConstraintViolation<CourseStatus> violation : violations) {
//      String propertyPath = violation.getPropertyPath().toString();
//      String message = violation.getMessage();
//      violationMessages.put(propertyPath, message);
//    }
//
//    assertTrue(violationMessages.containsKey("status"));
//    assertEquals("null は許可されていません", violationMessages.get("status"));
//
//  }

}
