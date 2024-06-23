package raisetech.student.management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.services.StudentService;

@RestController
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;
  private List<StudentDetail> studentsDetails;

  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentsCourses = service.searchStudentsCourseList();
    studentsDetails = converter.convertStudentDetails(students, studentsCourses);
    return studentsDetails;
  }

  @GetMapping("/students/past")
  public List<StudentDetail> getPastStudentList() {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentsCourses = service.searchStudentsCourseList();
    studentsDetails = converter.convertStudentDetails(students, studentsCourses).stream()
        .filter(studentDetail -> studentDetail.getStudent().isDeleted())
        .toList();

    return studentsDetails;
  }

  @GetMapping("/student")
  public StudentDetail getStudent(@RequestParam int id) {
    return service.searchStudent(id);
  }

  @PostMapping("/students/new")
  public ResponseEntity<String> registerStudent(@RequestBody StudentDetail studentDetail) {
    service.registerStudent(studentDetail);
    return ResponseEntity.ok("新規登録処理が成功しました");
  }

  @PostMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました");
  }
}
