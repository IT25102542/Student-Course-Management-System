package com.courseRegistration.studentRegistration.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String paymentMethod,
        String cardHolderName
) {}
