package com.courseRegistration.studentRegistration.controller;


@RestController
@RequestMapping("/api/lecturers")
@CrossOrigin(origins = "*")
public class LecturerController {
    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody LecturerRegistrationRequest request) {
        return lecturerService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LecturerLoginRequest request) {
        return lecturerService.login(request);
    }

    @GetMapping
    public List<Lecturer> getAllLecturers() {
        return lecturerService.getAllLecturers();
    }

    @GetMapping("/{id}")
    public Lecturer getLecturerById(@PathVariable Long id) {
        return lecturerService.getLecturerById(id);
    }

    @PutMapping("/{id}")
    public Lecturer updateLecturer(@PathVariable Long id, @RequestBody Lecturer lecturer) {
        return lecturerService.updateLecturer(id, lecturer);
    }

    @PutMapping("/{id}/profile")
    public Lecturer updateProfile(@PathVariable Long id, @RequestBody LecturerProfileUpdateRequest request) {
        return lecturerService.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiMessage deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return new ApiMessage("Lecturer deleted successfully");
    }

    @GetMapping("/{id}/dashboard")
    public LecturerDashboardDTO getDashboard(@PathVariable Long id) {
        return lecturerService.getDashboard(id);
    }
}
