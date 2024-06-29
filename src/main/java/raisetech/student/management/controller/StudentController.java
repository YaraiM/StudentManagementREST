package raisetech.student.management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.services.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@RestController
public class StudentController {

  private final StudentService service;

  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生一覧検索です。 全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生一覧（全件）
   */
  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /**
   * 過去の受講生一覧検索です。
   *
   * @return 過去の受講生一覧（全件）
   */
  @GetMapping("/students/past")
  public List<StudentDetail> getPastStudentList() {
    return service.searchPastStudentList();
  }

  /**
   * 受講生検索です。 IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生IDに紐づく受講生の詳細情報
   */
  @GetMapping("/students/detail")
  public StudentDetail getStudent(@RequestParam int id) {
    return service.searchStudent(id);
  }

  /**
   * 受講生の新規登録です。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録が成功した受講生の情報
   */
  @PostMapping("/students/new")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody StudentDetail studentDetail) {
    StudentDetail newStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(newStudentDetail);
  }

  /**
   * 受講生の更新です。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 更新が成功した場合に「更新処理が成功しました」と表示
   */
  @PostMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました");
  }
}
