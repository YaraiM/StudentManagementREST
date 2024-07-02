package raisetech.student.management.model.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
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
  List<Student> searchStudents();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return IDに紐づく受講生の情報
   */
  Student searchStudent(int id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報（全件）
   */
  List<StudentCourse> searchStudentCoursesList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return IDに紐づく受講生のコース情報
   */
  List<StudentCourse> searchStudentCourses(int studentId);

  /**
   * 受講生の新規登録です。 新規の受講生の情報を受講生テーブルに追加します。
   *
   * @param student 新規受講生の情報
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報の新規登録です。 新規の受講生コースの情報を受講生コーステーブルに追加します。
   *
   * @param studentCourse 新規受講生に登録するコース情報
   */
  void registerStudentCourses(StudentCourse studentCourse);

  /**
   * 受講生情報の更新です。受講生IDを参照して、受講生テーブルで該当する受講生情報を更新します。
   *
   * @param student 受講生の更新情報
   */
  void updateStudent(Student student);

  /**
   * 受講生のコース名の更新です。受講生IDを参照して、受講生コーステーブルで該当する受講生のコース名を更新します。
   *
   * @param studentCourse 受講生のコースの更新情報
   */
  void updateStudentCourses(StudentCourse studentCourse);
}
