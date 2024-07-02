package raisetech.student.management.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.ErrorResponse;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.services.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@RestController
@Validated
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
   * 受講生の詳細情報の検索です。 IDに紐づく任意の受講生の情報を取得します。 存在しないIDをパラメータに指定してリクエストすると、404NotFoundを返します。
   *
   * @param id 受講生ID（入力チェック：1~1000まで）
   * @return 受講生IDに紐づく受講生の詳細情報
   */
  @GetMapping("/students/detail")
  public StudentDetail getStudent(@RequestParam @NotNull int id) {
    return service.searchStudent(id);
  }

  /**
   * 受講生の詳細情報の新規登録です。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録が成功した受講生の詳細情報
   */
  @PostMapping("/students/new")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail newStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(newStudentDetail);
  }

  /**
   * 受講生の詳細情報の更新です。 キャンセルフラグの更新もここで行います。（論理削除）
   *
   * @param studentDetail 受講生の詳細情報
   * @return 更新が成功した場合に「更新処理が成功しました」と表示
   */
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました");
  }

  /**
   * 存在しないIDをパラメータ指定した場合に例外処理を行うメソッドです。
   * ResourceNotFoundExceptionがスローされたときに呼び出され、ステータスコード404（NotFound）の例外メッセージを返します。
   *
   * @param ex 例外クラス（データが存在しない）
   * @return 例外メッセージ
   */
  @ExceptionHandler(ResourceNotFoundException.class) // ResourceNotFoundExceptionがスローされたときに呼び出す
  @ResponseStatus(HttpStatus.NOT_FOUND)  // ステータスコードを404に設定
  public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
    return new ErrorResponse(ex.getMessage());
  }
}

