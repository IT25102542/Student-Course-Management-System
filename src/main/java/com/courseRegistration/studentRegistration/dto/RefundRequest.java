package com.courseRegistration.studentRegistration.dto;

public record RefundRequest(
        String reason,
        String initiatedBy
) {}
