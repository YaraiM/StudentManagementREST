package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
  @Operation(summary = "受講生一覧検索", description = "受講生の一覧を検索します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = StudentDetail.class))
          )
      )
  })
  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /**
   * 過去の受講生一覧検索です。
   *
   * @return 過去の受講生一覧（全件）
   */
  @Operation(summary = "過去の受講生一覧検索", description = "過去の受講生の一覧を検索します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = StudentDetail.class))
          )
      )
  })
  @GetMapping("/students/past")

  public List<StudentDetail> getPastStudentList() {
    return service.searchPastStudentList();
  }

  /**
   * 受講生の詳細情報の検索です。 IDに紐づく任意の受講生の詳細情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生IDに紐づく受講生の詳細情報
   */
  @Operation(summary = "受講生の詳細情報検索", description = "IDに紐づく任意の受講生の詳細情報を取得します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDetail.class))
      ),
      @ApiResponse(responseCode = "404", description = "存在しないIDを指定した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping("/students/detail")
  public StudentDetail getStudent(
      @Parameter(description = "受講生のID") @RequestParam @NotNull int id)
      throws ResourceNotFoundException {
    return service.searchStudent(id);
  }

  /**
   * 受講生の詳細情報の新規登録です。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録が成功した受講生の詳細情報
   */
  @Operation(summary = "受講生新規登録", description = "受講生の詳細情報を新規登録します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDetail.class))
      )
  })
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
  @Operation(summary = "受講生更新", description = "受講生の詳細情報を更新します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "更新処理が成功しました"))
      )
  })
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました");
  }

}
