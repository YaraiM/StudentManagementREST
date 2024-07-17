package raisetech.student.management.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raisetech.student.management.model.data.Gender.男性;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
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

  /**
   * テスト用にStudentDetailオブジェクトを作成するメソッドです。受講生IDのみ入力したインスタンスが生成されます。
   *
   * @param id 受講生ID
   * @return 受講生の詳細情報
   */
  private static StudentDetail createTestStudentDetail(int id) {
    Student student = new Student();
    student.setId(id);

    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setStudentId(student.getId());
    studentCourse2.setStudentId(student.getId());
    List<StudentCourse> studentCourses = new ArrayList<>(List.of(studentCourse1, studentCourse2));

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);
    return studentDetail;
  }

  @Test
  void 受講生詳細の一覧検索_エンドポイントでサービスの処理が適切に呼び出されて処理成功のレスポンスが返ってくること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.get("/students"))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 過去の受講生詳細の一覧検索_エンドポイントでサービスの処理が適切に呼び出されて処理成功のレスポンスが返ってくること()
      throws Exception {
    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.get("/students/past"))
        .andExpect(status().isOk());

    verify(service, times(1)).searchPastStudentList();
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
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ResourceNotFoundException))
        .andExpect(result -> assertEquals("受講生ID 「" + id + "」は存在しません",
            result.getResolvedException().getMessage()));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生の新規登録_エンドポイントでサービスの処理が適切に呼び出され空で返ってくること()
      throws Exception {
    // リクエストデータを実際のリクエストデータと同様にJSONで入力し、入力チェックの検証も兼ねている。
    // 本来であれば返りは登録されたデータが入るが、モック化すると意味がないため、レスポンスは作らない。
    // 実際のテストコードは、プロダクトコードと並行して作る（Postmanの動作確認前）ので、JSON形式の入力チェックをやっておく。

    // 実行と検証
    mockMvc.perform(
            MockMvcRequestBuilders.post("/students/new").contentType(MediaType.APPLICATION_JSON)
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
  void 受講生の更新_エンドポイントでサービスの処理が適切に呼び出され_更新処理が成功しました_というメッセージが返ってくること()
      throws Exception {

    // 実行と検証
    mockMvc.perform(MockMvcRequestBuilders.put("/students/update")
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
                                "age": 56,
                                "gender": "男性",
                                "remark": "更新のテストです。年齢を56に、deletedをtrueにしています。",
                                "deleted": true
                            },
                            "studentCourses": [
                                {
                                    "id": 1,
                                    "courseName": "Java"
                                },
                                {
                                    "id":2,
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
  void 入力チェック_リクエスト可能な情報がすべて適切な場合に入力チェックがかからないこと()
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
  void 入力チェック_リクエスト可能な情報のうち入力値の指定がある項目が不適切な場合に入力チェックがかかること()
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

  }

}
