package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.dto.AdminDashboardDTO;
import com.courseRegistration.studentRegistration.dto.StudentSummaryDTO;
import com.courseRegistration.studentRegistration.dto.TransactionSummaryDTO;
import com.courseRegistration.studentRegistration.model.Enrollment;
import com.courseRegistration.studentRegistration.model.Payment;
import com.courseRegistration.studentRegistration.model.Student;
import com.courseRegistration.studentRegistration.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
    }

    public AdminDashboardDTO getDashboardSummary() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        long totalEnrollments = allEnrollments.size();

        double totalRevenue = allEnrollments.stream()
                .map(e -> e.getPayment().getAmount())
                .mapToDouble(java.math.BigDecimal::doubleValue)
                .sum();

        long recentRegistrations = enrollmentRepository.findAll().stream()
                .filter(e -> e.getRegisteredAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count();

        long pendingTransactions = paymentRepository.findAll().stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .count();

        return new AdminDashboardDTO(
                totalStudents, totalEnrollments, totalCourses,
                totalRevenue, recentRegistrations, pendingTransactions
        );
    }

    public List<StudentSummaryDTO> getAllStudentsSummary() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(student.getId());
                    int courseCount = studentEnrollments.size();
                    double totalPaid = studentEnrollments.stream()
                            .map(e -> e.getPayment().getAmount())
                            .mapToDouble(BigDecimal::doubleValue)
                            .sum();

                    return new StudentSummaryDTO(
                            student.getId(),
                            student.getName(),
                            student.getEmail(),
                            student.getPhoneNumber(),
                            courseCount,
                            totalPaid
                    );
                })
                .collect(Collectors.toList());
    }

    public List<TransactionSummaryDTO> getRecentTransactions() {
        return paymentRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getPaidAt().compareTo(p1.getPaidAt()))
                .limit(10)
                .map(payment -> {
                    // Find student and course for this payment
                    Enrollment enrollment = enrollmentRepository.findAll().stream()
                            .filter(e -> e.getPayment().getId().equals(payment.getId()))
                            .findFirst()
                            .orElse(null);

                    String studentName = enrollment != null ?
                            enrollment.getStudent().getName() : "Unknown";
                    String courseTitle = enrollment != null ?
                            enrollment.getCourse().getTitle() : "Unknown";

                    return new TransactionSummaryDTO(
                            payment.getTransactionReference(),
                            studentName,
                            courseTitle,
                            payment.getAmount().doubleValue(),
                            payment.getStatus(),
                            payment.getPaidAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public double getTotalRevenue() {
        return enrollmentRepository.findAll().stream()
                .map(e -> e.getPayment().getAmount())
                .mapToDouble(java.math.BigDecimal::doubleValue)
                .sum();
    }
}

