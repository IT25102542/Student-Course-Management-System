package com.courseRegistration.studentRegistration.controller;

import com.courseRegistration.studentRegistration.dto.AdminLoginRequest;
import com.courseRegistration.studentRegistration.dto.AdminRegistrationRequest;
import com.courseRegistration.studentRegistration.dto.ApiMessage;
import com.courseRegistration.studentRegistration.dto.AuthResponse;
import com.courseRegistration.studentRegistration.model.Admin;
import com.courseRegistration.studentRegistration.service.AdminService;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AdminRegistrationRequest request) {
        return adminService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    //for update Admin
    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.updateAdmin(id, admin);
    }

    //for delete Admin
    @DeleteMapping("/{id}")
    public ApiMessage deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return new ApiMessage("Admin deleted successfully");
    }
}
