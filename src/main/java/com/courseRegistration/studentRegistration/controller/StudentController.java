package com.courseRegistration.studentRegistration.controller;

import com.courseRegistration.studentRegistration.dto.ApiMessage;
import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.dto.CourseEnrollmentRequest;
import com.courseRegistration.studentRegistration.dto.RegistrationResponse;
import com.courseRegistration.studentRegistration.dto.StudentLoginRequest;
import com.courseRegistration.studentRegistration.dto.StudentProfileUpdateRequest;
import com.courseRegistration.studentRegistration.dto.StudentRegistrationRequest;
import com.courseRegistration.studentRegistration.model.Student;
import com.courseRegistration.studentRegistration.service.StudentService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody StudentRegistrationRequest request) {
        return studentService.register(request);
    }

    @PostMapping("/{id}/courses/register")
    public RegistrationResponse registerAnotherCourse(@PathVariable Long id,
                                                      @RequestBody CourseEnrollmentRequest request) {
        return studentService.enrollExistingStudent(id, request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody StudentLoginRequest request) {
        return studentService.login(request);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @PutMapping("/{id}/profile")
    public Student updateStudentProfile(@PathVariable Long id, @RequestBody StudentProfileUpdateRequest request) {
        return studentService.updateStudentProfile(id, request);
    }
    @DeleteMapping("/{id}")
    public ApiMessage deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ApiMessage("Student deleted successfully");
    }
}
