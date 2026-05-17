package com.courseRegistration.studentRegistration.service;

import com.courseRegistration.studentRegistration.exception.ApiException;
import com.courseRegistration.studentRegistration.model.Course;
import com.courseRegistration.studentRegistration.model.Lecturer;
import com.courseRegistration.studentRegistration.repository.CourseRepository;
import com.courseRegistration.studentRegistration.repository.EnrollmentRepository;
import com.courseRegistration.studentRegistration.repository.LecturerRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LecturerRepository lecturerRepository;

    public CourseService(CourseRepository courseRepository,
                         EnrollmentRepository enrollmentRepository,
                         LecturerRepository lecturerRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lecturerRepository = lecturerRepository;
    }

    public Course createCourse(Course course) {
        validateCourse(course);
        course.setCourseCode(course.getCourseCode().trim().toUpperCase());
        course.setLecturers(resolveLecturers(course));

        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new ApiException(HttpStatus.CONFLICT, "Course code already exists");
        }

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        Course course = getCourseById(id);
        validateCourse(updatedCourse);
        String newCode = updatedCourse.getCourseCode().trim().toUpperCase();

        courseRepository.findByCourseCode(newCode)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Course code already exists");
                });

        course.setCourseCode(newCode);
        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setSubjects(updatedCourse.getSubjects());
        course.setDuration(updatedCourse.getDuration());
        course.setCapacity(updatedCourse.getCapacity());
        course.setPrice(updatedCourse.getPrice());
        course.setActive(updatedCourse.isActive());
        course.setLecturers(resolveLecturers(updatedCourse));
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        if (enrollmentRepository.countByCourseId(id) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "This course has registered students. Make it inactive instead of deleting it.");
        }
        courseRepository.delete(course);
    }

    private void validateCourse(Course course) {
        requireText(course.getCourseCode(), "Course code is required");
        requireText(course.getTitle(), "Course title is required");
        if (course.getCapacity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Course capacity must be greater than zero");
        }
        if (course.getPrice() == null || course.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Course price must be zero or greater");
        }
    }

    private Set<Lecturer> resolveLecturers(Course course) {
        Set<Long> lecturerIds = new LinkedHashSet<>();

        if (course.getLecturerIds() != null) {
            lecturerIds.addAll(course.getLecturerIds());
        }
        if (lecturerIds.isEmpty() && course.getLecturers() != null) {
            course.getLecturers().stream()
                    .filter(lecturer -> lecturer.getId() != null)
                    .map(Lecturer::getId)
                    .forEach(lecturerIds::add);
        }

        if (lecturerIds.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Assign at least one lecturer to the course");
        }

        List<Lecturer> lecturers = lecturerRepository.findAllById(lecturerIds);
        if (lecturers.size() != lecturerIds.size()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "One or more selected lecturers were not found");
        }

        return new LinkedHashSet<>(lecturers);
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, message);
        }
    }
}


