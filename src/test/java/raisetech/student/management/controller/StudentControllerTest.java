package raisetech.student.management.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
    Student student = new Student();
    student.setId(id);

    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setStudentId(student.getId());
    studentCourse2.setStudentId(student.getId());
    List<StudentCourse> studentCourses = new ArrayList<>(List.of(studentCourse1, studentCourse2));

    StudentDetail studentDetail = new StudentDetail(student, studentCourses);
    when(service.searchStudent(id)).thenReturn(studentDetail);

    // 実行と検証
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/students/detail")
            .param("id", String.valueOf(id)))
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

}
