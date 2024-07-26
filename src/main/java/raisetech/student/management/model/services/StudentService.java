package raisetech.student.management.model.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Status;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.ResourceNotFoundException;
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
   * 受講生一覧検索です。 受講生の一覧と受講生のコース一覧とコース申込状況一覧をconverterで受講生詳細情報一覧に変換します。
   * 指定されたリクエストパラメータの値に応じてフィルタリングを行います。
   *
   * @return 受講生詳細情報一覧
   */
  public List<StudentDetail> searchStudentList(
      Boolean deleted) {
    List<Student> students = repository.searchStudents();
    List<StudentCourse> studentsCoursesList = repository.searchStudentCoursesList();
    List<CourseStatus> courseStatusesList = repository.searchCourseStatusesList();
    List<StudentDetail> studentDetails = converter.convertStudentDetails(students,
        studentsCoursesList,
        courseStatusesList);

    return studentDetails.stream()
        .filter(studentDetail ->
            deleted == null || studentDetail.getStudent().isDeleted() == deleted)
        .toList();

  }

  /**
   * 受講生検索です。 IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報と受講生コース情報に紐づくコース申込状況を取得し、受講生の情報を設定します。
   *
   * @param id 受講生ID
   * @return IDに紐づく受講生の詳細情報
   */
  public StudentDetail searchStudent(int id) throws ResourceNotFoundException {
    Student student = repository.searchStudent(id);

    if (student == null) {
      throw new ResourceNotFoundException("受講生ID 「" + id + "」は存在しません");
    }

    List<StudentCourse> studentCourses = repository.searchStudentCourses(student.getId());

    List<CourseStatus> courseStatuses = studentCourses.stream()
        .map(studentCourse -> repository.searchCourseStatuses(
            studentCourse.getId())) //studentCourseを対応するcourseStatusに変換
        .collect(Collectors.toList());

    return new StudentDetail(student, studentCourses, courseStatuses);

  }

  /**
   * 受講生の詳細情報の新規登録です。 受講生の詳細情報から受講生の情報と受講生のコース情報とコース申込状況を取り出し、それぞれ新規登録します。
   * 新規登録の際、コース情報に初期情報（受講生ID、コース開始日、終了日）を自動で設定します。 また、コース申込状況に初期情報（コースID、ステータス"仮申込"）を自動で設定します。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録される受講生の詳細情報
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.registerStudent(student);

    studentDetail.getStudentCourses().forEach(studentCourse -> {
      initStudentCourses(studentCourse, student);
      repository.registerStudentCourses(studentCourse);
    });

    studentDetail.getCourseStatuses()
        .forEach(courseStatus -> studentDetail.getStudentCourses().forEach(studentCourse -> {
          courseStatus.setCourseId(studentCourse.getId());
          courseStatus.setStatus(Status.仮申込);
          repository.registerCourseStatuses(courseStatus);
        }));

    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報（受講生ID、コース開始日、終了日）を登録するメソッドです。
   *
   * @param studentCourse 受講生コース情報
   * @param student       受講生情報
   */
  void initStudentCourses(StudentCourse studentCourse, Student student) {
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(student.getId());
    studentCourse.setStartDate(now);
    studentCourse.setEndDate(now.plusYears(1));
  }

  /**
   * 受講生の詳細情報の更新です。 受講生の詳細情報の受講生ID、受講生コースID、コース申込状況IDを参照して、 それぞれに紐づく受講生、受講生コース、コース申込状況を更新します。
   *
   * @param studentDetail 更新される受講生の詳細情報
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourses().forEach(repository::updateStudentCourses);
    studentDetail.getCourseStatuses().forEach(repository::updateCourseStatuses);
  }
}
