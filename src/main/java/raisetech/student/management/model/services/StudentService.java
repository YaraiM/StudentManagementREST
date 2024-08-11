package raisetech.student.management.model.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.converter.CourseConverter;
import raisetech.student.management.model.converter.StudentConverter;
import raisetech.student.management.model.data.CourseSearchCriteria;
import raisetech.student.management.model.data.CourseStatus;
import raisetech.student.management.model.data.Gender;
import raisetech.student.management.model.data.Status;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.data.StudentSearchCriteria;
import raisetech.student.management.model.domain.CourseDetail;
import raisetech.student.management.model.domain.IntegratedDetail;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.exception.EmailAlreadyExistsException;
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
        .filter(
            studentDetail -> doesStringContainSubstring(studentDetail.getStudent().getFullname(),
                criteria.getFullname()))

        .filter(
            studentDetail -> doesStringContainSubstring(studentDetail.getStudent().getFurigana(),
                criteria.getFurigana()))

        .filter(
            studentDetail -> doesStringContainSubstring(studentDetail.getStudent().getNickname(),
                criteria.getNickname()))

        .filter(studentDetail -> doesStringContainSubstring(studentDetail.getStudent().getMail(),
            criteria.getMail()))

        .filter(studentDetail -> doesStringContainSubstring(studentDetail.getStudent().getAddress(),
            criteria.getAddress()))

        .filter(studentDetail -> isIntegerMoreThan(studentDetail.getStudent().getAge(),
            criteria.getMinAge()))

        .filter(studentDetail -> isIntegerLessThan(studentDetail.getStudent().getAge(),
            criteria.getMaxAge()))

        .filter(studentDetail -> isGenderMatching(studentDetail.getStudent().getGender(),
            criteria.getGender()))

        .filter(studentDetail -> isBooleanEqual(studentDetail.getStudent().isDeleted(),
            criteria.getDeleted()))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> doesStringContainSubstring(studentCourse.getCourseName(),
                criteria.getCourseName())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isDateOnOrAfter(studentCourse.getStartDate().toLocalDate(),
                criteria.getStartDateFrom())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isDateOnOrBefore(studentCourse.getStartDate().toLocalDate(),
                criteria.getStartDateTo())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isDateOnOrAfter(studentCourse.getEndDate().toLocalDate(),
                criteria.getEndDateFrom())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isDateOnOrBefore(studentCourse.getStartDate().toLocalDate(),
                criteria.getEndDateFrom())))

        .toList();

  }

  /**
   * 受講生コース詳細一覧検索です。 受講生コースの一覧とコース申込状況一覧をcourseConverterでコース詳細情報一覧に変換します。
   * 指定されたリクエストパラメータの値に応じてフィルタリングを行います。
   *
   * @return コース詳細情報一覧
   */
  public List<CourseDetail> searchStudentCourseList(CourseSearchCriteria criteria) {
    List<StudentCourse> studentCoursesList = repository.searchStudentCoursesList();
    List<CourseStatus> courseStatusesList = repository.searchCourseStatusList();
    List<CourseDetail> courseDetails = courseConverter.convertCourseDetails(studentCoursesList,
        courseStatusesList);

    return courseDetails.stream()
        .filter(courseDetail -> doesStringContainSubstring(
            courseDetail.getStudentCourse().getCourseName(), criteria.getCourseName()))

        .filter(courseDetail -> isDateOnOrAfter(
            courseDetail.getStudentCourse().getStartDate().toLocalDate(),
            criteria.getStartDateFrom()))

        .filter(courseDetail -> isDateOnOrBefore(
            courseDetail.getStudentCourse().getStartDate().toLocalDate(),
            criteria.getStartDateTo()))

        .filter(courseDetail -> isDateOnOrAfter(
            courseDetail.getStudentCourse().getEndDate().toLocalDate(),
            criteria.getEndDateFrom()))

        .filter(courseDetail -> isDateOnOrBefore(
            courseDetail.getStudentCourse().getEndDate().toLocalDate(),
            criteria.getEndDateFrom()))

        .filter(courseDetail -> isStatusMatching(courseDetail.getCourseStatus().getStatus(),
            criteria.getStatus()))

        .toList();

  }

  private boolean doesStringContainSubstring(String targetValue, String criteriaValue) {
    return criteriaValue == null || targetValue.contains(criteriaValue);
  }

  private boolean isIntegerMoreThan(Integer targetValue, Integer criteriaValue) {
    return criteriaValue == null || targetValue >= criteriaValue;
  }

  private boolean isIntegerLessThan(Integer targetValue, Integer criteriaValue) {
    return criteriaValue == null || targetValue <= criteriaValue;
  }

  private boolean isBooleanEqual(Boolean targetValue, Boolean criteriaValue) {
    return criteriaValue == null || targetValue == criteriaValue;
  }

  private boolean isDateOnOrAfter(LocalDate targetDate, LocalDate criteriaDate) {
    return criteriaDate == null || targetDate.isAfter(criteriaDate);
  }

  private boolean isDateOnOrBefore(LocalDate targetDate, LocalDate criteriaDate) {
    return criteriaDate == null || targetDate.isBefore(criteriaDate);
  }

  private boolean isGenderMatching(Gender targetValue, Gender criteriaValue) {
    return criteriaValue == null || targetValue == criteriaValue;
  }

  private boolean isStatusMatching(Status criteriaValue, Status targetValue) {
    return criteriaValue == null || targetValue == criteriaValue;
  }

  /**
   * 受講生検索です。 IDに紐づく任意の受講生の情報を取得した後、その受講生に紐づく受講生コース情報を取得し、受講生の情報を設定します。
   *
   * @param id 受講生ID
   * @return IDに紐づく受講生の詳細情報
   */
  public StudentDetail searchStudent(int id) {
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
  public CourseDetail searchStudentCourse(int id) {
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

    for (Student studentAtDb : repository.searchStudents()) {
      if (studentAtDb.getMail().equals(student.getMail())) {
        throw new EmailAlreadyExistsException(
            "メールアドレス(" + student.getMail() + ")はすでに登録されているため使用できません。");
      }
    }

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
    int studentId = studentDetail.getStudent().getId();
    if (repository.searchStudent(studentId) == null) {
      throw new ResourceNotFoundException("受講生ID 「" + studentId + "」は存在しません");
    }

    for (StudentCourse studentCourse : studentDetail.getStudentCourses()) {
      if (repository.searchStudentCourse(studentCourse.getId()) == null) {
        throw new ResourceNotFoundException(
            "受講生コースID 「" + studentCourse.getId() + "」は存在しません");
      }
    }

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
    if (repository.searchStudentCourse(courseStatus.getCourseId()) == null) {
      throw new ResourceNotFoundException(
          "受講生コースID 「" + courseStatus.getCourseId() + "」は存在しません");
    }
    repository.updateCourseStatus(courseStatus);
  }

}
