package raisetech.student.management.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.domain.StudentDetail;
import raisetech.student.management.model.services.StudentService;

@Controller
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;
  private List<StudentDetail> studentsDetails; // コントローラーのフィールドとして空のstudentDetailを定義

  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/students")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentsCourses = service.searchStudentsCourseList();
    studentsDetails = converter.convertStudentDetails(students, studentsCourses).stream()
        .filter(studentDetail -> !studentDetail.getStudent().isDeleted())
        .toList();

    model.addAttribute("studentList", studentsDetails);
    return "studentList";
  }

  @GetMapping("/students/past")
  public String getPastStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentsCourses = service.searchStudentsCourseList();
    studentsDetails = converter.convertStudentDetails(students, studentsCourses).stream()
        .filter(studentDetail -> studentDetail.getStudent().isDeleted())
        .toList();

    model.addAttribute("pastStudentList", studentsDetails);
    return "pastStudentList";
  }

  @GetMapping("/students/new")
  public String newStudent(Model model) {
    model.addAttribute("studentDetail", new StudentDetail());
    return "registerStudent";
  }

  @GetMapping("/students/{id}")
  public String getStudent(@PathVariable int id, Model model) {
    StudentDetail studentDetail = service.searchStudent(id);
    model.addAttribute("studentDetail", studentDetail);
    return "updateStudent";
  }


  @PostMapping("/students/new")
  // ビューの登録フォームで入力されたstudentDetailの情報をstudentServiceに送る
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      return "registerStudent";
    }

    // フォームで入力されたstudentDetailのstudentの情報とstudentCourseの情報（最初の一つ）をserviceにあるregisterStudentメソッドの引数とする
    service.registerStudent(studentDetail);
    return "redirect:/students";
  }

  @PostMapping("/students/update")
  public String updateStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      return "updateStudent";
    }
    service.updateStudent(studentDetail);
    return "redirect:/students";
  }
}
