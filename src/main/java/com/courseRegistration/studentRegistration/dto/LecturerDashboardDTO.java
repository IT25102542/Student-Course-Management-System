package com.courseRegistration.studentRegistration.dto;

import java.util.List;

public record LecturerDashboardDTO(
        Long lecturerId,
        String name,
        String username,
        List<LecturerCourseDTO> courses
) {
}
