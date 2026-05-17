package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Enrollment;
import com.courseRegistration.studentRegistration.model.Payment;
import com.courseRegistration.studentRegistration.repository.EnrollmentRepository;

import java.time.LocalDateTime;
import java.util.List;

import com.courseRegistration.studentRegistration.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, PaymentRepository paymentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
    }
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
    // Add this method - for DELETE (Cancel Enrollment)
    public void cancelEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        // Check if within 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        if (enrollment.getRegisteredAt().isBefore(sevenDaysAgo)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel enrollment after 7 days. Please contact admin.");
        }

        // Update payment status to REFUNDED
        Payment payment = enrollment.getPayment();
        if (payment != null && "PAID".equals(payment.getStatus())) {
            payment.setStatus("REFUNDED");
            paymentRepository.save(payment);
        }

        // Delete the enrollment
        enrollmentRepository.delete(enrollment);
    }
}

