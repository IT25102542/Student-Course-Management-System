package com.courseRegistration.studentRegistration;

import com.courseRegistration.studentRegistration.model.Admin;
import com.courseRegistration.studentRegistration.model.Course;
import com.courseRegistration.studentRegistration.model.Lecturer;
import com.courseRegistration.studentRegistration.repository.AdminRepository;
import com.courseRegistration.studentRegistration.repository.CourseRepository;
import com.courseRegistration.studentRegistration.repository.LecturerRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;

    public DataSeeder(AdminRepository adminRepository,
                      CourseRepository courseRepository,
                      LecturerRepository lecturerRepository) {
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    public void run(String... args) {
        if (!adminRepository.existsByEmail("admin@site.com")) {
            adminRepository.save(new Admin("Default Admin", "admin@site.com", "admin123"));
        }

        Lecturer defaultLecturer = lecturerRepository.findByUsername("saman@lec.com")
                .or(() -> lecturerRepository.findByUsername("lecturer")
                        .map(lecturer -> {
                            lecturer.setUsername("saman@lec.com");
                            return lecturerRepository.save(lecturer);
                        }))
                .map(lecturer -> {
                    if ("Default Lecturer".equals(lecturer.getName())) {
                        lecturer.setName("Saman Kumara");
                        return lecturerRepository.save(lecturer);
                    }
                    return lecturer;
                })
                .orElseGet(() -> lecturerRepository.save(new Lecturer("Saman Kumara", "saman@lec.com", "lecturer123")));

        seedCourse("SE101", "Java Programming Basics",
                "Learn object oriented programming, classes, objects, inheritance, and simple Java projects.",
                "OOP Concepts, Java Classes, Inheritance, Mini Project",
                "8 weeks", 30, "15000.00", defaultLecturer);
        seedCourse("WD201", "Web Development with Bootstrap",
                "Build responsive websites using HTML, CSS, JavaScript, and Bootstrap components.",
                "HTML, CSS, JavaScript, Bootstrap UI",
                "6 weeks", 25, "12000.00", defaultLecturer);
        seedCourse("DB110", "Database Design with SQL",
                "Practice table design, relationships, CRUD operations, and SQL queries.",
                "ER Diagrams, SQL CRUD, Joins, MySQL Database",
                "5 weeks", 20, "10000.00", defaultLecturer);
        seedCourse("PY120", "Python Programming Fundamentals",
                "Learn Python syntax, control flow, functions, data structures, and practical scripting.",
                "Python Basics, Functions, Lists, Dictionaries, File Handling",
                "7 weeks", 28, "13000.00", defaultLecturer);

        backfillUnassignedCourses(defaultLecturer);
    }

    private void seedCourse(String code, String title, String description, String subjects, String duration,
                            int capacity, String price, Lecturer defaultLecturer) {
        if (!courseRepository.existsByCourseCode(code)) {
            Course course = new Course(code, title, description, subjects, duration, capacity, new BigDecimal(price));
            course.setLecturers(new LinkedHashSet<>(Set.of(defaultLecturer)));
            courseRepository.save(course);
        } else {
            courseRepository.findByCourseCode(code).ifPresent(course -> {
                if (course.getLecturers() == null || course.getLecturers().isEmpty()) {
                    course.setLecturers(new LinkedHashSet<>(Set.of(defaultLecturer)));
                    courseRepository.save(course);
                }
            });
        }
    }

    private void backfillUnassignedCourses(Lecturer defaultLecturer) {
        courseRepository.findAll().forEach(course -> {
            if (course.getLecturers() == null || course.getLecturers().isEmpty()) {
                course.setLecturers(new LinkedHashSet<>(Set.of(defaultLecturer)));
                courseRepository.save(course);
            }
        });
    }
}
