package com.courseRegistration.studentRegistration.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        BigDecimal amount,
        String paymentMethod,
        String cardHolderName,
        String transactionReference,
        String status,
        LocalDateTime paidAt
) {}