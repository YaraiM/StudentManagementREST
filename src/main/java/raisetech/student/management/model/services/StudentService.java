package raisetech.student.management.model.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.converter.CourseConverter;
import raisetech.student.management.model.converter.StudentConverter;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.IntegratedDetail;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.ResourceNotFoundException;
import raisetech.student.management.model.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。 受講生の検索や登録や更新処理を行います。
 */
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentConverter studentConverter;
  private final CourseConverter courseConverter;

  public StudentService(StudentRepository repository, StudentConverter studentConverter,
      CourseConverter courseConverter) {
    this.repository = repository;
    this.studentConverter = studentConverter;
    this.courseConverter = courseConverter;
  }

  /**
   * 受講生一覧検索です。 受講生の一覧と受講生のコース一覧をconverterで受講生詳細情報一覧に変換します。 指定されたリクエストパラメータの値に応じてフィルタリングを行います。
   *
   * @return 受講生詳細情報一覧
   */
  public List<StudentDetail> searchStudentList(StudentSearchCriteria criteria) {
    List<Student> students = repository.searchStudents();
    List<StudentCourse> studentsCoursesList = repository.searchStudentCoursesList();
    List<StudentDetail> studentDetails = studentConverter.convertStudentDetails(students,
        studentsCoursesList);

    return studentDetails.stream()
        .filter(studentDetail -> meetsStudentSearchCriteria(studentDetail, criteria))
        .toList();

  }

  /**
   * 受講生一覧検索を行う際のフィルタリングロジックです。 指定された検索条件が受講生詳細情報と合致しない場合、if文が実行され、falseを返します。
   * 検索条件が指定されていない、もしくは指定された検索条件が受講生詳細情報と合致している場合、if文は実行されず、スキップされます。 すべてのif文がスキップされた場合、trueを返します。
   *
   * @param studentDetail 受講生の詳細情報
   * @param criteria      受講生の検索条件
   * @return 検索条件に合致したかどうかの真偽値
   */
  private boolean meetsStudentSearchCriteria(StudentDetail studentDetail,
      StudentSearchCriteria criteria) {

    if (criteria.getFullname() != null &&
        !studentDetail.getStudent().getFullname().contains(criteria.getFullname())) {
      return false;
    }

    if (criteria.getFurigana() != null &&
        !studentDetail.getStudent().getFurigana().contains(criteria.getFurigana())) {
      return false;
    }

    if (criteria.getNickname() != null &&
        !studentDetail.getStudent().getNickname().contains(criteria.getNickname())) {
      return false;
    }

    if (criteria.getMail() != null &&
        !studentDetail.getStudent().getMail().contains(criteria.getMail())) {
      return false;
    }

    if (criteria.getAddress() != null &&
        !studentDetail.getStudent().getAddress().contains(criteria.getAddress())) {
      return false;
    }

    if (criteria.getMinAge() != null &&
        studentDetail.getStudent().getAge() < criteria.getMinAge()) {
      return false;
    }

    if (criteria.getMaxAge() != null &&
        studentDetail.getStudent().getAge() > criteria.getMaxAge()) {
      return false;
    }

    if (criteria.getGender() != null &&
        studentDetail.getStudent().getGender() != criteria.getGender()) {
      return false;
    }

    if (criteria.getDeleted() != null &&
        studentDetail.getStudent().isDeleted() != criteria.getDeleted()) {
      return false;
    }

    if (criteria.getCourseName() != null &&
        studentDetail.getStudentCourses().stream()
            .noneMatch(course -> course.getCourseName().contains(criteria.getCourseName()))) {
      return false;
    }

    if (criteria.getStartDateFrom() != null &&
        studentDetail.getStudentCourses().stream()
            .noneMatch(course -> course.getStartDate().toLocalDate()
                .isAfter(criteria.getStartDateFrom()))) {
      return false;
    }

    if (criteria.getStartDateTo() != null &&
        studentDetail.getStudentCourses().stream()
            .noneMatch(course -> course.getStartDate().toLocalDate()
                .isBefore(criteria.getStartDateTo()))) {
      return false;
    }

    if (criteria.getEndDateFrom() != null &&
        studentDetail.getStudentCourses().stream()
            .noneMatch(course -> course.getEndDate().toLocalDate()
                .isAfter(criteria.getEndDateFrom()))) {
      return false;
    }

    if (criteria.getEndDateTo() != null &&
        studentDetail.getStudentCourses().stream()
            .noneMatch(course -> course.getEndDate().toLocalDate()
                .isBefore(criteria.getEndDateTo()))) {
      return false;
    }

    return true;

  }

  /**
   * 受講生コース詳細一覧検索です。 受講生コースの一覧とコース申込状況一覧をcourseConverterでコース詳細情報一覧に変換します。
   * リクエストパラメータに申込状況を指定して絞り込み検索することができます。
   *
   * @return コース詳細情報一覧
   */
  public List<CourseDetail> searchStudentCourseList(CourseSearchCriteria criteria) {
    List<StudentCourse> studentCoursesList = repository.searchStudentCoursesList();
    List<CourseStatus> courseStatusesList = repository.searchCourseStatusList();
    List<CourseDetail> courseDetails = courseConverter.convertCourseDetails(studentCoursesList,
        courseStatusesList);

    return courseDetails.stream()
        .filter(courseDetail -> meetsCourseSearchCriteria(courseDetail, criteria))
        .toList();

  }

  /**
   * 受講生コース一覧検索を行う際のフィルタリングのロジックです。 指定された検索条件がコース詳細情報と合致しない場合、if文が実行され、falseを返します。
   * 検索条件が指定されていない、もしくは指定された検索条件がコース詳細情報と合致している場合、if文は実行されず、スキップされます。 すべてのif文がスキップされた場合、trueを返します。
   *
   * @param courseDetail コースの詳細情報
   * @param criteria     コースの検索条件
   * @return 検索条件に合致したかどうかの真偽値
   */
  private boolean meetsCourseSearchCriteria(CourseDetail courseDetail,
      CourseSearchCriteria criteria) {

    if (criteria.getCourseName() != null &&
        !courseDetail.getStudentCourse().getCourseName().contains(criteria.getCourseName())) {
      return false;
    }

    if (criteria.getStartDateFrom() != null &&
        !courseDetail.getStudentCourse().getStartDate().toLocalDate()
            .isAfter(criteria.getStartDateFrom())) {
      return false;
    }

    if (criteria.getStartDateTo() != null &&
        !courseDetail.getStudentCourse().getStartDate().toLocalDate()
            .isBefore(criteria.getStartDateTo())) {
      return false;
    }

    if (criteria.getEndDateFrom() != null &&
        !courseDetail.getStudentCourse().getEndDate().toLocalDate()
            .isAfter(criteria.getEndDateFrom())) {
      return false;
    }

    if (criteria.getEndDateTo() != null &&
        !courseDetail.getStudentCourse().getEndDate().toLocalDate()
            .isBefore(criteria.getEndDateTo())) {
      return false;
    }

    if (criteria.getStatus() != null &&
        courseDetail.getCourseStatus().getStatus() != criteria.getStatus()) {
      return false;
    }

    return true;

  }

  /**
   * 受講生検索です。 IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報を取得し、受講生の情報を設定します。
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

    return new StudentDetail(student, studentCourses);

  }

  /**
   * 受講生コース検索です。 IDに紐づく任意の受講生コースの情報を取得した後、そのコースに紐づく申込状況を取得し、受講生コースの詳細情報を設定します。
   *
   * @param id 受講生コースID
   * @return IDに紐づく受講生コースの詳細情報
   */
  public CourseDetail searchStudentCourse(int id) throws ResourceNotFoundException {
    StudentCourse studentCourse = repository.searchStudentCourse(id);

    if (studentCourse == null) {
      throw new ResourceNotFoundException("受講生コースID 「" + id + "」は存在しません");
    }

    CourseStatus courseStatus = repository.searchCourseStatus(studentCourse.getId());

    return new CourseDetail(studentCourse, courseStatus);

  }

  /**
   * 受講生の詳細情報の新規登録です。 受講生の詳細情報から受講生の情報と受講生のコース情報を取り出し、それぞれ新規登録します。
   * 新規登録の際、コース情報に初期情報（受講生ID、コース開始日、終了日）を自動で設定します。 また、コース申込状況に受講生コースIDを設定します。
   *
   * @param studentDetail 受講生の詳細情報
   * @return 新規登録される受講生の詳細とコース詳細の統合情報
   */
  @Transactional
  public IntegratedDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.registerStudent(student);

    List<CourseDetail> courseDetails = new ArrayList<>();

    studentDetail.getStudentCourses().forEach(studentCourse -> {
      initStudentCourses(studentCourse, student);
      repository.registerStudentCourses(studentCourse);

      CourseStatus courseStatus = new CourseStatus();
      courseStatus.setCourseId(studentCourse.getId());
      repository.registerCourseStatus(courseStatus);

      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentCourse(studentCourse);
      courseDetail.setCourseStatus(courseStatus);

      courseDetails.add(courseDetail);

    });

    return new IntegratedDetail(studentDetail, courseDetails);

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
   * 受講生の詳細情報の更新です。 指定した受講生詳細情報に紐づく受講生および受講生コースを更新します。
   *
   * @param studentDetail 更新される受講生の詳細情報
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourses().forEach(repository::updateStudentCourses);
  }

  /**
   * コース申込状況の更新です。指定したコース申込状況に紐づく情報を更新します。
   *
   * @param courseStatus コース申込状況
   */
  @Transactional
  public void updateCourseStatus(CourseStatus courseStatus) {
    repository.updateCourseStatus(courseStatus);
  }

}
