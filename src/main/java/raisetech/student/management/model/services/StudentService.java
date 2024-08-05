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
        .filter(studentDetail -> doesFullnameContain(criteria.getFullname(),
            studentDetail.getStudent().getFullname()))

        .filter(studentDetail -> doesFuriganaContain(criteria.getFurigana(),
            studentDetail.getStudent().getFurigana()))

        .filter(studentDetail -> doesNicknameContain(criteria.getNickname(),
            studentDetail.getStudent().getNickname()))

        .filter(studentDetail -> doesMailContain(criteria.getMail(),
            studentDetail.getStudent().getMail()))

        .filter(studentDetail -> doesAddressContain(criteria.getAddress(),
            studentDetail.getStudent().getAddress()))

        .filter(studentDetail -> isAgeAtLeast(criteria.getMinAge(),
            studentDetail.getStudent().getAge()))

        .filter(studentDetail -> isAgeLessThan(criteria.getMaxAge(),
            studentDetail.getStudent().getAge()))

        .filter(studentDetail -> doesGenderMatch(criteria.getGender(),
            studentDetail.getStudent().getGender()))

        .filter(studentDetail -> isDeleted(criteria.getDeleted(),
            studentDetail.getStudent().isDeleted()))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> doesCourseNameContain(criteria.getCourseName(),
                studentCourse.getCourseName())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isStartDateOnOrAfter(criteria.getStartDateFrom(),
                studentCourse.getStartDate().toLocalDate())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isStartDateOnOrBefore(criteria.getStartDateTo(),
                studentCourse.getStartDate().toLocalDate())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isEndDateOnOrAfter(criteria.getEndDateFrom(),
                studentCourse.getEndDate().toLocalDate())))

        .filter(studentDetail -> studentDetail.getStudentCourses()
            .stream()
            .anyMatch(studentCourse -> isEndDateOnOrBefore(criteria.getEndDateFrom(),
                studentCourse.getStartDate().toLocalDate())))

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
        .filter(courseDetail -> doesCourseNameContain(criteria.getCourseName(),
            courseDetail.getStudentCourse().getCourseName()))

        .filter(courseDetail -> isStartDateOnOrAfter(criteria.getStartDateFrom(),
            courseDetail.getStudentCourse().getStartDate().toLocalDate()))

        .filter(courseDetail -> isStartDateOnOrBefore(criteria.getStartDateTo(),
            courseDetail.getStudentCourse().getStartDate().toLocalDate()))

        .filter(courseDetail -> isEndDateOnOrAfter(criteria.getEndDateFrom(),
            courseDetail.getStudentCourse().getEndDate().toLocalDate()))

        .filter(courseDetail -> isEndDateOnOrBefore(criteria.getEndDateFrom(),
            courseDetail.getStudentCourse().getEndDate().toLocalDate()))

        .filter(courseDetail -> doesStatusMatch(criteria.getStatus(),
            courseDetail.getCourseStatus().getStatus()))

        .toList();

  }

  private boolean doesFullnameContain(String criteriaFullname, String targetFullname) {
    return criteriaFullname == null || targetFullname.contains(criteriaFullname);
  }

  private boolean doesFuriganaContain(String criteriaFurigana, String targetFurigana) {
    return criteriaFurigana == null || targetFurigana.contains(criteriaFurigana);
  }

  private boolean doesNicknameContain(String criteriaNickname, String targetNickname) {
    return criteriaNickname == null || targetNickname.contains(criteriaNickname);
  }

  private boolean doesMailContain(String criteriaMail, String targetMail) {
    return criteriaMail == null || targetMail.contains(criteriaMail);
  }

  private boolean doesAddressContain(String criteriaAddress, String targetAddress) {
    return criteriaAddress == null || targetAddress.contains(criteriaAddress);
  }

  private boolean isAgeAtLeast(Integer criteriaMinAge, Integer targetAge) {
    return criteriaMinAge == null || targetAge >= criteriaMinAge;
  }

  private boolean isAgeLessThan(Integer criteriaMaxAge, Integer targetAge) {
    return criteriaMaxAge == null || targetAge <= criteriaMaxAge;
  }

  private boolean doesGenderMatch(Gender criteriaGender, Gender targetGender) {
    return criteriaGender == null || targetGender == criteriaGender;
  }

  private boolean isDeleted(Boolean criteriaDeleted, Boolean targetDeleted) {
    return criteriaDeleted == null || targetDeleted == criteriaDeleted;
  }

  private boolean doesCourseNameContain(String criteriaCourseName, String targetCourseName) {
    return criteriaCourseName == null || targetCourseName.contains(criteriaCourseName);
  }

  private boolean isStartDateOnOrAfter(LocalDate criteriaStartDateFrom, LocalDate targetStartDate) {
    return criteriaStartDateFrom == null || targetStartDate.isAfter(criteriaStartDateFrom);
  }

  private boolean isStartDateOnOrBefore(LocalDate criteriaStartDateTo, LocalDate targetStartDate) {
    return criteriaStartDateTo == null || targetStartDate.isAfter(criteriaStartDateTo);
  }

  private boolean isEndDateOnOrAfter(LocalDate criteriaEndDateFrom, LocalDate targetEndDate) {
    return criteriaEndDateFrom == null || targetEndDate.isAfter(criteriaEndDateFrom);
  }

  private boolean isEndDateOnOrBefore(LocalDate criteriaEndDateTo, LocalDate targetEndDate) {
    return criteriaEndDateTo == null || targetEndDate.isAfter(criteriaEndDateTo);
  }

  private boolean doesStatusMatch(Status criteriaStatus, Status targetStatus) {
    return criteriaStatus == null || targetStatus == criteriaStatus;
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
