package raisetech.student.management.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "統合情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntegratedDetail {

  @Valid
  private StudentDetail studentDetail;

  @Valid
  private List<CourseDetail> courseDetails;

}
