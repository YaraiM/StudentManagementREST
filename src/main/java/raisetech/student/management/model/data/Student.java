package raisetech.student.management.model.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生情報のオブジェクトです。
 */
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
