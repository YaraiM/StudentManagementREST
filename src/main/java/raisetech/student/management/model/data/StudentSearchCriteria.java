package raisetech.student.management.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.model.validation.ValidGender;

@Schema(description = "受講生一覧検索時に指定可能なパラメータ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchCriteria {

  @Schema(description = "受講生の氏名（部分一致）")
  private String fullname;

  @Schema(description = "受講生のふりがな（部分一致）")
  private String furigana;

  @Schema(description = "受講生のニックネーム（部分一致）")
  private String nickname;

  @Schema(description = "受講生のメールアドレス（部分一致）")
  @Email
  private String mail;

  @Schema(description = "受講生の住所（部分一致）")
  private String address;

  @Schema(description = "検索対象の下限年齢（●歳以上）")
  private Integer minAge;

  @Schema(description = "検索対象の上限年齢（●歳以下）")
  private Integer maxAge;

  @Schema(description = "受講生の性別（男性、女性、その他　のいずれか）")
  @ValidGender
  private Gender gender;

  @Schema(description = "受講生の削除フラグ")
  private Boolean deleted;

  @Schema(description = "受講しているコース名（部分一致）")
  private String courseName;

}
