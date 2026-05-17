package com.courseRegistration.studentRegistration.dto;

public record StudentProfileUpdateRequest(
        String fullName,
        String password,
        String phoneNumber
) {
}

