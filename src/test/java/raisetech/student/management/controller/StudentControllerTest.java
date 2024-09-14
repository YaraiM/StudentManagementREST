package raisetech.student.management.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raisetech.student.management.model.data.Gender.男性;
import static raisetech.student.management.model.data.Status.仮申込;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.EmailAlreadyExistsException;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.services.StudentService;

@WebMvcTest(StudentController.class) //Spring MVCのうち、Web層（特にコントローラ層）の単体テストに使用される
class StudentControllerTest {

  @Autowired
  MockMvc mockMvc; //Spring MVCのエンドポイントをテストするためのモックオブジェクト

  @Autowired
  private ObjectMapper objectMapper; // オブジェクトをJSON文字列に変換するためのオブジェクト

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @BeforeAll
  public static void setUp() {
    Locale.setDefault(new Locale("ja", "JP"));
  }

  /**
   * テスト用にStudentDetailオブジェクトを作成するメソッドです。受講生IDのみセットされたインスタンスが生成されます。
   *
   * @param id 受講生ID
   * @return 受講生の詳細情報
   */
  private static StudentDetail createTestStudentDetail(int id) {
    Student student = new Student();
    student.setId(id);

    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setId(1);
    studentCourse2.setId(2);
    studentCourse1.setStudentId(student.getId());
    studentCourse2.setStudentId(student.getId());
    List<StudentCourse> studentCourses = new ArrayList<>(List.of(studentCourse1, studentCourse2));

    return new StudentDetail(student, studentCourses);
  }

  /**
   * テスト用にCourseDetailオブジェクトを作成するメソッドです。受講生コースIDのみセットされたインスタンスが生成されます。
   *
   * @param id 受講生コースID
   * @return 受講生コースの詳細情報
   */
  private static CourseDetail createTestCourseDetail(int id) {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(id);

    return new CourseDetail(studentCourse, courseStatus);
  }

  @Test
  void 受講生詳細の一覧検索_エンドポイントでサービスの処理が適切に呼び出されて処理成功のレスポンスが返ってくること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(
            MockMvcRequestBuilders.get("/students"))
        .andExpect(status().isOk());

    // 検証：適切な型の引数を入力した際に１回実行されることを確認
    verify(service, times(1)).searchStudentList(any(StudentSearchCriteria.class));
  }

  @Test
  void 受講生コース詳細の一覧検索_エンドポイントでサービスの処理が適切に呼び出されて処理成功のレスポンスが返ってくること()
      throws Exception {
    // 事前準備
    CourseSearchCriteria criteria = new CourseSearchCriteria();

    // 実行と検証
    mockMvc.perform(
            MockMvcRequestBuilders.get("/students/courses"))
        .andExpect(status().isOk());

    // 検証：適切な型の引数を入力した際に１回実行されることを確認
    verify(service, times(1)).searchStudentCourseList(any(CourseSearchCriteria.class));
  }

  @Test
  void 受講生詳細の検索_正常系_存在する受講生IDを指定したときにエンドポイントでサービスの処理が適切に呼び出され指定したIDに紐づくstudentDetailが返ってくること()
      throws Exception {
    // 事前準備
    int id = 666;
    StudentDetail studentDetail = createTestStudentDetail(id);
    when(service.searchStudent(id)).thenReturn(studentDetail);

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.get("/students/detail").param("id", String.valueOf(id)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.student.id").value(id))
        .andExpect(jsonPath("$.studentCourses", hasSize(2)))
        .andExpect(jsonPath("$.studentCourses[0].studentId").value(id))
        .andExpect(jsonPath("$.studentCourses[1].studentId").value(id));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生詳細の検索_異常系_存在しない受講生IDを指定したときに例外がスローされること()
      throws Exception {
    // 事前準備
    int id = 999;
    when(service.searchStudent(id)).thenThrow(
        new ResourceNotFoundException("受講生ID 「" + id + "」は存在しません"));

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.get("/students/detail")
            .param("id", String.valueOf(id)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("受講生ID 「" + id + "」は存在しません"));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生コース詳細の検索_正常系_存在する受講生コースIDを指定したときにエンドポイントでサービスの処理が適切に呼び出され指定したIDに紐づくcourseDetailが返ってくること()
      throws Exception {
    // 事前準備
    int id = 666;
    CourseDetail courseDetail = createTestCourseDetail(id);
    when(service.searchStudentCourse(id)).thenReturn(courseDetail);

    // 実行と検証
    mockMvc.perform(
            MockMvcRequestBuilders.get("/students/courses/detail").param("id", String.valueOf(id)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.studentCourse.id").value(id))
        .andExpect(jsonPath("$.courseStatus.courseId").value(id));

    verify(service, times(1)).searchStudentCourse(id);
  }

  @Test
  void 受講生コース詳細の検索_異常系_存在しない受講生IDを指定したときに例外がスローされること()
      throws Exception {
    // 事前準備
    int id = 999;
    when(service.searchStudentCourse(id)).thenThrow(
        new ResourceNotFoundException("受講生コースID 「" + id + "」は存在しません"));

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.get("/students/courses/detail")
            .param("id", String.valueOf(id)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("受講生コースID 「" + id + "」は存在しません"));

    verify(service, times(1)).searchStudentCourse(id);
  }

  @Test
  void 受講生の新規登録_正常系_エンドポイントでサービスの処理が適切に呼び出され空で返ってくること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(
            post("/students/new").contentType(MediaType.APPLICATION_JSON)
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
                ))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生の新規登録_異常系_すでに登録されているメールアドレスを指定したときに例外がスローされること()
      throws Exception {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();
    Student student = new Student();
    student.setMail("error@example.com");
    studentDetail.setStudent(student);

    String expectedErrorMessage = "メールアドレス(" + studentDetail.getStudent().getMail()
        + ")はすでに登録されているため使用できません。";

    when(service.registerStudent(any(StudentDetail.class))).thenThrow(
        new EmailAlreadyExistsException(expectedErrorMessage));

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
                            "mail": "error@example.com",
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
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));

  }

  @Test
  void 受講生の更新_正常系_エンドポイントでサービスの処理が適切に呼び出され_更新処理が成功しました_というメッセージが返ってくること()
      throws Exception {

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                        {
                            "student": {
                                "id": 1,
                                "fullname": "田中昭三",
                                "furigana": "たなかしょうぞう",
                                "nickname": "ショーゾー",
                                "mail": "shozo@example.com",
                                "address": "東京",
                                "age": 56,
                                "gender": "男性",
                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
                                "deleted": true
                            },
                            "studentCourses": [
                                {
                                    "id": 1,
                                    "student_id": 1,
                                    "courseName": "Java"
                                },
                                {
                                    "id":2,
                                    "student_id": 1,
                                    "courseName": "Ruby"
                                }
                            ]
                        }
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました"));

    verify(service, times(1)).updateStudent(any());

  }

  @Test
  void 受講生の更新_異常系_存在しない受講生IDを指定したときに例外をスローすること()
      throws Exception {
    // 事前準備
    Student student = new Student();
    student.setId(999);
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);

    String expectedErrorMessage =
        "受講生ID 「" + studentDetail.getStudent().getId() + "」は存在しません";

    doThrow(new ResourceNotFoundException(expectedErrorMessage))
        .when(service).updateStudent(any(StudentDetail.class));

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                        {
                            "student": {
                                "id": 999,
                                "fullname": "田中昭三",
                                "furigana": "たなかしょうぞう",
                                "nickname": "ショーゾー",
                                "mail": "shozo@example.com",
                                "address": "東京",
                                "age": 56,
                                "gender": "男性",
                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
                                "deleted": true
                            },
                            "studentCourses": [
                                {
                                    "id": 1,
                                    "student_id": 999,
                                    "courseName": "Java"
                                },
                                {
                                    "id":2,
                                    "student_id": 999,
                                    "courseName": "Ruby"
                                }
                            ]
                        }
                    """
            ))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));

  }

  @Test
  void 受講生の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること()
      throws Exception {
    // 事前準備
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(999);
    List<StudentCourse> studentCourses = new ArrayList<>();
    studentCourses.add(studentCourse);
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudentCourses(studentCourses);

    String expectedErrorMessage =
        "受講生コースID 「" + studentDetail.getStudentCourses().getFirst().getId()
            + "」は存在しません";

    doThrow(new ResourceNotFoundException(expectedErrorMessage))
        .when(service).updateStudent(any(StudentDetail.class));

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                        {
                            "student": {
                                "id": 1,
                                "fullname": "田中昭三",
                                "furigana": "たなかしょうぞう",
                                "nickname": "ショーゾー",
                                "mail": "shozo@example.com",
                                "address": "東京",
                                "age": 56,
                                "gender": "男性",
                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
                                "deleted": true
                            },
                            "studentCourses": [
                                {
                                    "id": 1,
                                    "student_id": 1,
                                    "courseName": "Java"
                                },
                                {
                                    "id":999,
                                    "student_id": 1,
                                    "courseName": "Ruby"
                                }
                            ]
                        }
                    """
            ))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));

  }

  @Test
  void コース申込状況の更新_正常系_エンドポイントでサービスの処理が適切に呼び出され_更新処理が成功しました_というメッセージが返ってくること()
      throws Exception {

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/courses/statuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {"courseId":1, "status":"受講中"}
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました"));

    verify(service, times(1)).updateCourseStatus(any());

  }

  @Test
  void コース申込状況の更新_異常系_存在しない受講生コースIDを指定したときに例外をスローすること()
      throws Exception {
    // 事前準備
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(999);

    String expectedErrorMessage =
        "受講生コースID 「" + courseStatus.getCourseId() + "」は存在しません";

    doThrow(new ResourceNotFoundException(expectedErrorMessage))
        .when(service).updateCourseStatus(any(CourseStatus.class));

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/courses/statuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {"courseId":999, "status":"受講中"}
                    """
            ))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));

  }

  @Test
  void 受講生詳細情報の入力チェック_リクエスト可能な情報がすべて適切な場合に入力チェックがかからないこと()
      throws Exception {
    // 事前準備
    int id = 666;
    StudentDetail studentDetail = createTestStudentDetail(id);

    studentDetail.getStudent().setFullname("田中昭三");
    studentDetail.getStudent().setFurigana("たなかしょうぞう");
    studentDetail.getStudent().setNickname("ショーゾー");
    studentDetail.getStudent().setMail("shozo@example.com");
    studentDetail.getStudent().setAddress("東京");
    studentDetail.getStudent().setAge(55);
    studentDetail.getStudent().setGender(男性);
    studentDetail.getStudent().setRemark("新規登録のテストです");
    studentDetail.getStudent().setDeleted(false);

    studentDetail.getStudentCourses().get(0).setCourseName("Java");
    studentDetail.getStudentCourses().get(1).setCourseName("Ruby");

    Set<ConstraintViolation<StudentDetail>> violations = validator.validate(studentDetail);

    // 検証
    assertEquals(0, violations.size());

  }

  @Test
  void 受講生詳細情報の入力チェック_Gender以外のリクエスト可能な情報のうち入力値の指定がある項目が不適切な場合に入力チェックがかかること()
      throws Exception {
    // 事前準備
    int id = 666;
    StudentDetail studentDetail = createTestStudentDetail(id);

    studentDetail.getStudent().setFullname(""); // fullnameは空白を許容しない
    studentDetail.getStudent().setFurigana(""); // fuliganaは空白を許容しない
    studentDetail.getStudent().setNickname("ショーゾー");
    studentDetail.getStudent().setMail("shozo&example.com"); // mailはメールアドレス以外の入力値を許容しない
    studentDetail.getStudent().setAddress("東京");
    studentDetail.getStudent().setAge(55);
    studentDetail.getStudent().setGender(男性);
    studentDetail.getStudent().setRemark("新規登録のテストです");
    studentDetail.getStudent().setDeleted(false);

    studentDetail.getStudentCourses().get(0).setCourseName(""); // courseNameは空白を許容しない
    studentDetail.getStudentCourses().get(1).setCourseName(""); // courseNameは空白を許容しない

    Set<ConstraintViolation<StudentDetail>> violations = validator.validate(studentDetail);

    // 検証
    assertEquals(5, violations.size());

    Map<String, String> violationMessages = new HashMap<>();
    for (ConstraintViolation<StudentDetail> violation : violations) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      violationMessages.put(propertyPath, message);
    }

    assertTrue(violationMessages.containsKey("student.fullname"));
    assertEquals("空白は許可されていません", violationMessages.get("student.fullname"));

    assertTrue(violationMessages.containsKey("student.furigana"));
    assertEquals("空白は許可されていません", violationMessages.get("student.furigana"));

    assertTrue(violationMessages.containsKey("student.mail"));
    assertEquals("電子メールアドレスとして正しい形式にしてください",
        violationMessages.get("student.mail"));

    assertTrue(violationMessages.containsKey("studentCourses[0].courseName"));
    assertEquals("空白は許可されていません", violationMessages.get("studentCourses[0].courseName"));

    assertTrue(violationMessages.containsKey("studentCourses[1].courseName"));
    assertEquals("空白は許可されていません", violationMessages.get("studentCourses[1].courseName"));

  }

  @Test
  void コース申込状況の入力チェック_リクエスト可能な情報がすべて適切な場合に入力チェックがかからないこと()
      throws Exception {
    // 事前準備
    CourseStatus courseStatus = new CourseStatus();

    courseStatus.setId(111);
    courseStatus.setCourseId(222);
    courseStatus.setStatus(仮申込);

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    // 検証
    assertEquals(0, violations.size());

  }

  @Test
  void コース申込状況の入力チェック_リクエスト可能な情報のうち入力値の指定がある項目が不適切な場合に入力チェックがかかること()
      throws Exception {
    // 事前準備
    CourseStatus courseStatus = new CourseStatus();

    courseStatus.setId(111); // ユーザーがリクエストできない情報のため検証不要
    courseStatus.setCourseId(222); // ユーザーがリクエストできない情報のため検証不要
    courseStatus.setStatus(null); // statusはnullを許容しない

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    // 検証
    assertEquals(1, violations.size());

    Map<String, String> violationMessages = new HashMap<>();
    for (ConstraintViolation<CourseStatus> violation : violations) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      violationMessages.put(propertyPath, message);
    }

    assertTrue(violationMessages.containsKey("status"));
    assertEquals("null は許可されていません", violationMessages.get("status"));

  }

}
