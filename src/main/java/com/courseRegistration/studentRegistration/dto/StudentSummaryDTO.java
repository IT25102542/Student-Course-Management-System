package com.courseRegistration.studentRegistration.dto;

public record StudentSummaryDTO(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        int enrolledCoursesCount,
        double totalPaid
) {}
