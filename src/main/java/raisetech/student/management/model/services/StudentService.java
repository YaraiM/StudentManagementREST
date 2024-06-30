package raisetech.student.management.model.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。 受講生の検索や登録や更新処理を行います。
 */
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentConverter converter;

  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生一覧検索です。 受講生の一覧と受講生のコース一覧をconverterで受講生詳細情報一覧に変換します。
   *
   * @return 受講生一覧（全件）
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> students = repository.searchStudents();
    List<StudentCourse> studentsCourses = repository.searchStudentsCoursesList();
    return converter.convertStudentDetails(students, studentsCourses);
  }

  /**
   * 過去の受講生一覧検索です。 受講生のうち、deleted属性がtrueの受講生を検索します。
   *
   * @return 過去の受講生一覧
   */
  public List<StudentDetail> searchPastStudentList() {
    List<Student> students = repository.searchStudents();
    List<StudentCourse> studentsCourses = repository.searchStudentsCoursesList();
    return converter.convertStudentDetails(students, studentsCourses).stream()
        .filter(studentDetail -> studentDetail.getStudent().isDeleted())
        .toList();
  }

  /**
   * 受講生検索です。 IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報を取得し、受講生の情報を設定します。
   *
   * @param id 受講生ID
   * @return IDに紐づく受講生の詳細情報
   */
  public StudentDetail searchStudent(int id) {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentsCourses = repository.searchStudentsCourses(student.getId());
    return new StudentDetail(student, studentsCourses);
  }

  /**
   * 受講生の新規登録です。 受講生の詳細情報から受講生の情報と受講生のコース情報を取り出し、それぞれ新規登録します。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録される受講生の詳細情報
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    repository.registerStudent(studentDetail.getStudent());
    for (StudentCourse studentCourse : studentDetail.getStudentCourses()) {
      studentCourse.setStudentId(studentDetail.getStudent().getId());
      studentCourse.setStartDate(LocalDateTime.now());
      studentCourse.setEndDate(LocalDateTime.now().plusYears(1));
      repository.registerStudentCourse(studentCourse);
    }
    return studentDetail;
  }

  /**
   * 受講生の更新です。 受講生の詳細情報の受講生IDおよび受講生コースIDを参照して、 それぞれに紐づく受講生および受講生コース情報を更新します。
   *
   * @param studentDetail 更新される受講生の詳細情報
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    for (StudentCourse studentCourse : studentDetail.getStudentCourses()) {
      repository.updateStudentCourse(studentCourse);
    }
  }
}
