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
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return 受講生一覧（全件）
   */
  @Select("SELECT * FROM students")
  List<Student> searchStudents();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return IDに紐づく受講生の情報
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(int id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報（全件）
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentsCoursesList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return IDに紐づく受講生のコース情報
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> searchStudentsCourses(int studentId);

  /**
   * 新規の受講生の情報を受講生テーブルに追加します。
   *
   * @param student 新規受講生の情報
   */
  @Insert(
      "INSERT INTO students(fullname, furigana, nickname, mail, address, age, gender, remark, deleted)"
          + "values(#{fullname}, #{furigana}, #{nickname}, #{mail}, #{address}, #{age}, #{gender}, #{remark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * 新規の受講生コースの情報を受講生コーステーブルに追加します。
   *
   * @param studentCourse 新規受講生に登録するコース情報
   */
  @Insert("INSERT INTO students_courses(student_id, course_name, start_date, end_date)"
      + "values(#{studentId}, #{courseName}, #{startDate}, #{endDate})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生IDを参照して、該当する受講生の情報を受講生テーブルから検索し、その受講生情報を更新します。
   *
   * @param student 受講生の更新情報
   */
  @Update("UPDATE students SET fullname=#{fullname}, furigana=#{furigana}, nickname=#{nickname}, mail=#{mail}, address=#{address}, age=#{age}, gender=#{gender}, remark=#{remark}, deleted=#{deleted} WHERE id=#{id}")
  void updateStudent(Student student);

  /**
   * 受講生IDを参照して、該当する受講生のコース情報を受講生コーステーブルから検索し、その受講生コース情報を更新します。
   *
   * @param studentCourse 受講生のコースの更新情報
   */
  @Update("UPDATE students_courses SET course_name=#{courseName} WHERE id=#{id}")
  void updateStudentCourse(StudentCourse studentCourse);
}
