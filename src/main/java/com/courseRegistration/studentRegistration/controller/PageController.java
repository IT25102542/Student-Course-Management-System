package com.courseRegistration.studentRegistration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/register", "/register/"})
    public String registerPage() {
        return "forward:/register.html";
    }

    @GetMapping({"/login", "/login/"})
    public String loginPage() {
        return "forward:/login.html";
    }

    @GetMapping({"/dashboard", "/dashboard/"})
    public String dashboardPage() {
        return "forward:/dashboard.html";
    }

    @GetMapping({"/admin", "/admin/"})
    public String adminPage() {
        return "forward:/admin.html";
    }

    @GetMapping({"/lecturer-dashboard", "/lecturer-dashboard/"})
    public String lecturerDashboardPage() {
        return "forward:/lecturer-dashboard.html";
    }
}
