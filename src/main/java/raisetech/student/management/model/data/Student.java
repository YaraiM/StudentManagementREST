package raisetech.student.management.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生情報のオブジェクトです。
 */
@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {

  private int id;

  @NotBlank
  private String fullname;

  @NotBlank
  private String furigana;

  private String nickname;

  @NotBlank
  @Email
  private String mail;

  private String address;

  private Integer age;

  private Gender gender;

  private String remark;

  private boolean deleted;

}
