package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.dto.PaymentReceiptDTO;
import com.courseRegistration.studentRegistration.dto.PaymentRequest;
import com.courseRegistration.studentRegistration.dto.RefundRequest;
import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Payment;
import com.courseRegistration.studentRegistration.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Create a new payment
    public Payment createPayment(PaymentRequest request) {
        validatePaymentRequest(request);

        Payment payment = new Payment(
                request.amount(),
                request.paymentMethod(),
                request.cardHolderName(),
                generateTransactionReference()
        );

        return paymentRepository.save(payment);
    }

    // Get payment by ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    // Get payment by transaction reference
    public Payment getPaymentByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionReference(transactionRef)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    // Get all payments for a student
    public List<Payment> getPaymentsByStudentId(Long studentId) {
        return paymentRepository.findPaymentsByStudentId(studentId);
    }

    // Process refund
    public Payment processRefund(Long paymentId, RefundRequest request) {
        Payment payment = getPaymentById(paymentId);

        if (!"PAID".equals(payment.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only paid payments can be refunded");
        }

        payment.setStatus("REFUNDED");


        return paymentRepository.save(payment);
    }

    // Generate receipt
    public PaymentReceiptDTO generateReceipt(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        return new PaymentReceiptDTO(
                payment.getTransactionReference(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getCardHolderName(),
                payment.getStatus(),
                payment.getPaidAt(),
                "Payment for Course Registration"
        );
    }

    // Validate payment request
    private void validatePaymentRequest(PaymentRequest request) {
        if (request.amount() == null || request.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }
        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment method is required");
        }
        if (request.cardHolderName() == null || request.cardHolderName().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Card holder name is required");
        }
    }

    // Generate unique transaction reference
    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                + "-" + System.currentTimeMillis() % 10000;
    }
}