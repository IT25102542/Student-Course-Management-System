package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.dto.CourseEnrollmentRequest;
import com.courseRegistration.studentRegistration.dto.RegistrationResponse;
import com.courseRegistration.studentRegistration.dto.StudentLoginRequest;
import com.courseRegistration.studentRegistration.dto.StudentProfileUpdateRequest;
import com.courseRegistration.studentRegistration.dto.StudentRegistrationRequest;
import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Course;
import com.courseRegistration.studentRegistration.model.Enrollment;
import com.courseRegistration.studentRegistration.model.Payment;
import com.courseRegistration.studentRegistration.model.Student;
import com.courseRegistration.studentRegistration.repository.CourseRepository;
import com.courseRegistration.studentRegistration.repository.EnrollmentRepository;
import com.courseRegistration.studentRegistration.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(StudentRepository studentRepository,
                          CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public RegistrationResponse register(StudentRegistrationRequest request) {
        validateRegistration(request);

        if (studentRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "This student email is already registered");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Selected course was not found"));

        if (!course.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected course is not currently active");
        }

        long registeredCount = enrollmentRepository.countByCourseId(course.getId());
        if (registeredCount >= course.getCapacity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected course is already full");
        }

        // Create student object
        Student student = new Student(
                request.fullName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.age()
        );

        String customStudentId = generateStudentId();
        student.setStudentId(customStudentId);

        Student savedStudent = studentRepository.save(student);

        Payment payment = new Payment(
                course.getPrice(),
                request.paymentMethod(),
                request.cardHolderName(),
                "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );

        Enrollment enrollment = enrollmentRepository.save(new Enrollment(savedStudent, course, payment));

        return new RegistrationResponse(
                enrollment.getId(),
                savedStudent.getId(),
                savedStudent.getName(),
                course.getTitle(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionReference(),
                "Student registered, course selected, and payment completed. Student ID: " + customStudentId
        );
    }

    @Transactional
    public RegistrationResponse enrollExistingStudent(Long studentId, CourseEnrollmentRequest request) {
        Student student = getStudentById(studentId);
        requireText(request.paymentMethod(), "Payment method is required");
        requireText(request.cardHolderName(), "Card holder name is required");

        if (request.courseId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please select a course");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Selected course was not found"));

        if (!course.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected course is not currently active");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, course.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Student is already registered for this course");
        }

        long registeredCount = enrollmentRepository.countByCourseId(course.getId());
        if (registeredCount >= course.getCapacity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected course is already full");
        }

        Payment payment = new Payment(
                course.getPrice(),
                request.paymentMethod(),
                request.cardHolderName(),
                "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );

        Enrollment enrollment = enrollmentRepository.save(new Enrollment(student, course, payment));

        return new RegistrationResponse(
                enrollment.getId(),
                student.getId(),
                student.getName(),
                course.getTitle(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionReference(),
                "Course registered and payment completed"
        );
    }

    public AuthResponse login(StudentLoginRequest request) {
        requireText(request.email(), "Student email is required");
        requireText(request.password(), "Student password is required");
        validateEmail(request.email());
        validatePassword(request.password());

        Student student = studentRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid student login details"));

        return new AuthResponse(student.getId(), student.getName(), student.getEmail(), "STUDENT",
                "Student login successful");
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);
        requireText(updatedStudent.getName(), "Student name is required");
        requireText(updatedStudent.getEmail(), "Student email is required");
        requireText(updatedStudent.getPassword(), "Student password is required");
        requireText(updatedStudent.getPhoneNumber(), "Student phone number is required");
        validateEmail(updatedStudent.getEmail());
        validatePassword(updatedStudent.getPassword());
        validatePhoneNumber(updatedStudent.getPhoneNumber());

        if (studentRepository.existsByEmailAndIdNot(updatedStudent.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "This student email is already registered");
        }

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setPassword(updatedStudent.getPassword());
        student.setPhoneNumber(updatedStudent.getPhoneNumber());
        student.setAge(updatedStudent.getAge());
        return studentRepository.save(student);
    }

    public Student updateStudentProfile(Long id, StudentProfileUpdateRequest request) {
        Student student = getStudentById(id);
        requireText(request.fullName(), "Student name is required");
        requireText(request.phoneNumber(), "Student phone number is required");
        validatePhoneNumber(request.phoneNumber());

        student.setName(request.fullName());
        student.setPhoneNumber(request.phoneNumber());

        if (request.password() != null && !request.password().isBlank()) {
            validatePassword(request.password());
            student.setPassword(request.password());
        }

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        enrollmentRepository.deleteAll(enrollments);
        studentRepository.delete(student);
    }

    private void validateRegistration(StudentRegistrationRequest request) {
        requireText(request.fullName(), "Student name is required");
        requireText(request.email(), "Student email is required");
        requireText(request.password(), "Student password is required");
        requireText(request.phoneNumber(), "Student phone number is required");
        requireText(request.paymentMethod(), "Payment method is required");
        requireText(request.cardHolderName(), "Card holder name is required");
        validateEmail(request.email());
        validatePassword(request.password());
        validatePhoneNumber(request.phoneNumber());

        if (request.age() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Student age must be greater than zero");
        }
        if (request.courseId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please select a course");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please enter a valid email address");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phone number must contain exactly 10 numbers");
        }
    }

    private String generateStudentId() {
        // Get current year
        String year = String.valueOf(java.time.Year.now().getValue()).substring(2);

        // Get total count of existing students
        long count = studentRepository.count();

        return String.format("SD%s%03d", year, count + 1);
    }
}
