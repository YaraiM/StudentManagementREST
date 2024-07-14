package raisetech.student.management.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.model.services.StudentService;

@WebMvcTest(StudentController.class) //Spring MVCのうち、Web層（特にコントローラ層）の単体テストに使用される
class StudentControllerTest {

  @Autowired
  MockMvc mockMvc; //Spring MVCのエンドポイントをテストするためのモックオブジェクト

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
  
}
