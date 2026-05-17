package com.courseRegistration.studentRegistration.dto;

public record CourseEnrollmentRequest(
        Long courseId,
        String paymentMethod,
        String cardHolderName
) {
}

