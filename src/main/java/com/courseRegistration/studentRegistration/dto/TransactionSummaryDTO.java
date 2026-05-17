package com.courseRegistration.studentRegistration.dto;

import java.time.LocalDateTime;

public record TransactionSummaryDTO(
        String transactionReference,
        String studentName,
        String courseTitle,
        double amount,
        String status,
        LocalDateTime paidAt
) {}