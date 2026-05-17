package com.courseRegistration.studentRegistration.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentReceiptDTO(
        String transactionReference,
        BigDecimal amount,
        String paymentMethod,
        String cardHolderName,
        String status,
        LocalDateTime paidAt,
        String description
) {}