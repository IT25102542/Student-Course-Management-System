package com.courseRegistration.studentRegistration.dto;

public record AdminRegistrationRequest(String name, String email, String password, Long creatorAdminId) {
}
