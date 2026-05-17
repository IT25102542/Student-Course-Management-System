package com.courseRegistration.studentRegistration.dto;

import java.time.LocalDateTime;

public record LecturerStudentDTO(
        Long id,
        String studentId,
        String name,
        String email,
        String phoneNumber,
        String status,
        LocalDateTime registeredAt
) {
}
