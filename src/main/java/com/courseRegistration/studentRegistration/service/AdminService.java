package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.dto.AdminLoginRequest;
import com.courseRegistration.studentRegistration.dto.AdminRegistrationRequest;
import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Admin;
import com.courseRegistration.studentRegistration.repository.AdminRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AuthResponse register(AdminRegistrationRequest request) {
        requireText(request.name(), "Admin name is required");
        requireText(request.email(), "Admin email is required");
        requireText(request.password(), "Admin password is required");

        if (request.creatorAdminId() == null || !adminRepository.existsById(request.creatorAdminId())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Only a logged in admin can add another admin");
        }

        if (adminRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "This admin email is already registered");
        }

        Admin admin = adminRepository.save(new Admin(request.name(), request.email(), request.password()));
        return new AuthResponse(admin.getId(), admin.getName(), admin.getEmail(), "ADMIN",
                "Admin registered successfully");
    }

    public AuthResponse login(AdminLoginRequest request) {
        requireText(request.email(), "Admin email is required");
        requireText(request.password(), "Admin password is required");

        Admin admin = adminRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid admin login details"));

        return new AuthResponse(admin.getId(), admin.getName(), admin.getEmail(), "ADMIN",
                "Admin login successful");
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, message);
        }
    }

    // for update Admin
    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin not found"));

        requireText(updatedAdmin.getName(), "Admin name is required");
        requireText(updatedAdmin.getEmail(), "Admin email is required");

        // Check if email is already taken by another admin
        if (!admin.getEmail().equals(updatedAdmin.getEmail())
                && adminRepository.existsByEmail(updatedAdmin.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "This admin email is already registered");
        }

        admin.setName(updatedAdmin.getName());
        admin.setEmail(updatedAdmin.getEmail());

        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isBlank()) {
            if (updatedAdmin.getPassword().length() < 6) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
            }
            admin.setPassword(updatedAdmin.getPassword());
        }

        return adminRepository.save(admin);
    }

    //for delete Admin
    public void deleteAdmin(Long id) {
        long adminCount = adminRepository.count();
        if (adminCount <= 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cannot delete the only admin account. At least one admin must exist.");
        }

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin not found"));

        adminRepository.delete(admin);
    }

}




