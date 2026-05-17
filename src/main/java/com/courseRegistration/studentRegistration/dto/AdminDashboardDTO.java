package com.courseRegistration.studentRegistration.dto;

public record AdminDashboardDTO(
        long totalStudents,
        long totalEnrollments,
        long totalCourses,
        double totalRevenue,
        long recentRegistrations,
        long pendingTransactions
) {}