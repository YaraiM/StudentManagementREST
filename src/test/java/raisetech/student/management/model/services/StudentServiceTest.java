package raisetech.student.management.model.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.model.data.Student;
import raisetech.student.management.model.data.StudentCourse;
import raisetech.student.management.model.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
    //共通の事前準備を設定
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理を適切に呼び出せていること() {
    // 事前準備
    List<Student> students = new ArrayList<>();
    List<StudentCourse> studentCoursesList = new ArrayList<>();
    when(repository.searchStudents()).thenReturn(
        students); // searchStudentsが呼び出されたら強制的にstudents（空のリスト）を返す
    when(repository.searchStudentCoursesList()).thenReturn(studentCoursesList);

    // 実行
    sut.searchStudentList(); //テスト対象のメソッドを呼び出す

    // 検証:verifyは呼び出されているかどうかを確認するときに使う。メソッドが期待する回数呼び出されたかを確認。
    verify(repository, times(1)).searchStudents();
    verify(repository, times(1)).searchStudentCoursesList();
    verify(converter, times(1)).convertStudentDetails(students, studentCoursesList);

    // 後処理：データベース弄る操作の場合（Postなど）、その処理をなかったことにする

  }
}
