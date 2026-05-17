package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionReference(String transactionReference);

    @Query("SELECT p FROM Payment p JOIN Enrollment e ON p.id = e.payment.id WHERE e.student.id = :studentId")
    List<Payment> findPaymentsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN Enrollment e ON p.id = e.payment.id WHERE e.student.id = :studentId")
    java.math.BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

    List<Payment> findByStatus(String status);
}