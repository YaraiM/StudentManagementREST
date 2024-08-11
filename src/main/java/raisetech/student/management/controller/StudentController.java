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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.IntegratedDetail;
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
   * 受講生一覧検索です。リクエストパラメータを指定することにより、絞りこみ検索できます。
   * ModelAttributeアノテーションによりStudentSearchCriteriaにリクエストパラメータがバインドされ、パラメータの入力は任意となります。
   *
   * @param criteria フィルタリングの基準値（＝検索条件）
   * @return 受講生詳細情報一覧
   */
  @Operation(summary = "受講生一覧検索", description = "条件に合致する受講生の一覧を検索します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = StudentDetail.class))
          )
      ),
      @ApiResponse(responseCode = "400", description = "無効な検索条件を指定した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))
          )
      )
  })
  @GetMapping("/students")
  public List<StudentDetail> getStudentList(@Valid @ModelAttribute StudentSearchCriteria criteria) {

    return service.searchStudentList(criteria);

  }

  /**
   * 受講生コースの一覧検索です。コースの申込状況を確認できます。
   *
   * @return 受講生コース詳細情報一覧
   */
  @Operation(summary = "受講生コース一覧検索", description = "条件に合致する受講生コースの一覧を検索します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = CourseDetail.class))
          )
      )
  })
  @GetMapping("/students/courses")
  public List<CourseDetail> getStudentCoursesList(
      @Valid @ModelAttribute CourseSearchCriteria criteria) {

    return service.searchStudentCourseList(criteria);

  }

  /**
   * 受講生の詳細情報の検索です。 IDに紐づく任意の受講生の詳細情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生IDに紐づく受講生の詳細情報
   * @throws ResourceNotFoundException 存在しないIDを指定した場合の例外
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
   * 受講生コースの詳細情報の検索です。 IDに紐づく任意のコースの申込状況を取得します。
   *
   * @param id 受講生コースID
   * @return 受講生コースの詳細情報（申込状況）
   * @throws ResourceNotFoundException 存在しないIDを指定した場合の例外
   */
  @Operation(summary = "受講生コースの詳細情報検索", description = "IDに紐づく任意の受講生コースの詳細情報を取得します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetail.class))
      ),
      @ApiResponse(responseCode = "404", description = "存在しないIDを指定した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping("/students/courses/detail")
  public CourseDetail getStudentCourses(
      @Parameter(description = "受講生コースのID") @RequestParam @NotNull int id)
      throws ResourceNotFoundException {
    return service.searchStudentCourse(id);
  }

  /**
   * 受講生の詳細情報の新規登録です。コースの申込状況は「仮登録」として自動登録されます。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録が成功した受講生の詳細情報およびコース詳細情報が統合された情報
   */
  @Operation(summary = "受講生新規登録", description = "受講生の詳細情報を新規登録します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = IntegratedDetail.class))
      ),
      @ApiResponse(responseCode = "400", description = "登録情報に無効な入力形式の値を指定した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))
          )
      )
  })
  @PostMapping("/students/new")
  public ResponseEntity<IntegratedDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    IntegratedDetail newStudent = service.registerStudent(studentDetail);
    return ResponseEntity.ok(newStudent);
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
      ),
      @ApiResponse(responseCode = "400", description = "登録情報に無効な入力形式の値を指定した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))
          )
      )
  })
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました");
  }

  /**
   * 受講生コースの申込状況の更新です。
   *
   * @param courseStatus 受講生コースの申込状況
   * @return 更新が成功した場合に「更新処理が成功しました」と表示
   */
  @Operation(summary = "受講生コース申込状況の更新", description = "受講生コースの申込状況を更新します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "処理が成功した場合のレスポンス",
          content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "更新処理が成功しました"))
      ),
      @ApiResponse(responseCode = "400", description = "登録情報に無効な入力形式の値を指定した場合のレスポンス",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))
          )
      )
  })
  @PutMapping("/students/courses/statuses/update")
  public ResponseEntity<String> updateStudentCourse(@RequestBody @Valid CourseStatus courseStatus) {
    service.updateCourseStatus(courseStatus);
    return ResponseEntity.ok("更新処理が成功しました");
  }

}
