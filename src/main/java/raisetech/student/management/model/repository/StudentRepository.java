package raisetech.student.management.model.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;

/**
 * 受講生および受講生コースの情報をDBから取得する。
 */
@Mapper
public interface StudentRepository {

  /**
   * @return データベースから受講生の情報を全件取得する。
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  /**
   * @return データベースから受講生の情報をidで指定して取得する。
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(int id);

  /**
   * @return データベースから受講生のコース情報を全件取得する。
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentsCoursesList();

  /**
   * @return データベースから受講生のコース情報をid指定して取得する。
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> searchStudentsCourses(int studentId);

  /**
   * @return 受講生の情報をデータベースに登録する。
   */
  @Insert("INSERT students values(#{id}, #{fullname}, #{furigana}, #{nickname}, #{mail}, #{address}, #{age}, #{gender}, #{remark}, #{isDeleted})")
  // ↓SQLで自動生成されたidをStudentオブジェクトのidとして使用できるようにする。
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * @return 受講生コースの情報をデータベースに登録する。
   */
  @Insert("INSERT students_courses values(#{id}, #{studentId}, #{courseName}, #{startDate}, #{endDate})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * @return 受講生の情報を更新する。
   */
  @Update("UPDATE students SET fullname=#{fullname}, furigana=#{furigana}, nickname=#{nickname}, mail=#{mail}, address=#{address}, age=#{age}, gender=#{gender}, remark=#{remark}, deleted=#{deleted} WHERE id=#{id}")
  void updateStudent(Student student);

  /**
   * @return 受講生コースの情報を更新する。
   */
  @Update("UPDATE students_courses SET course_name=#{courseName} WHERE id=#{id}")
  void updateStudentCourse(StudentCourse studentCourse);
}
