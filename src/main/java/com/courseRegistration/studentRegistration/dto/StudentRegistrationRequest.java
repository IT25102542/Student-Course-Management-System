package com.courseRegistration.studentRegistration.dto;

public record StudentRegistrationRequest(
        String fullName,
        String email,
        String password,
        String phoneNumber,
        int age,
        Long courseId,
        String paymentMethod,
        String cardHolderName
) {
}
