package com.courseRegistration.studentRegistration.dto;

import java.util.List;

public record LecturerCourseDTO(
        Long id,
        String courseCode,
        String title,
        String duration,
        int capacity,
        int enrolledCount,
        List<LecturerStudentDTO> students
) {
}
