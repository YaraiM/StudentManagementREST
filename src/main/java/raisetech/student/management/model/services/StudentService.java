package raisetech.student.management.model.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.repository.StudentRepository;

@Service
public class StudentService {

  private final StudentRepository repository;

  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    return repository.search();
  }

  public StudentDetail searchStudent(int id) {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentsCourses = repository.searchStudentsCourses(student.getId());
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourse(studentsCourses);
    return studentDetail;
  }

  public List<StudentCourse> searchStudentsCourseList() {
    return repository.searchStudentsCoursesList();
  }

  @Transactional //トランザクション管理のために必須
  // StudentControllerから送られてきたstudentDetailオブジェクトの情報をrepositoryに送る。
  public void registerStudent(StudentDetail studentDetail) {
    repository.registerStudent(
        studentDetail.getStudent()); //まずstudentの情報をrepositoryのregisterStudentメソッドで登録（SQLでstudentsテーブルにINSERT）
    // この時点で、StudentServiceから流れてきたstudentCourseには、フォームで入力されたcourseName以外の情報が入っていない。
    for (StudentCourse studentCourse : studentDetail.getStudentCourse()) {
      //studentCourseにstudentIdの数値（＝stundentテーブルのid）をセット。外部キー制約があるため、これはstudentCourseをstudents_coursesにINSERTする前に行う必要がある。
      studentCourse.setStudentId(studentDetail.getStudent().getId());
      studentCourse.setStartDate(LocalDateTime.now()); //受講開始日時は、この瞬間のLocalDateTimeをセット
      studentCourse.setEndDate(LocalDateTime.now().plusYears(1)); //受講終了日時は、受講開始日時の1年後をセット
      repository.registerStudentCourse(
          studentCourse); //studentCourseのすべての情報が出そろったら、repositoryのregisterStudentCourseメソッドで登録（SQLでstudents_coursesテーブルにINSERT）
    }
  }

  @Transactional //トランザクション管理のために必須
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    for (StudentCourse studentCourse : studentDetail.getStudentCourse()) {
      repository.updateStudentCourse(studentCourse);
    }
  }
}
