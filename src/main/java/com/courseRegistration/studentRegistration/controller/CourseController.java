package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCode(String courseCode);

    Optional<Course> findByCourseCode(String courseCode);

    @Query("select distinct c from Course c join c.lecturers l where l.id = :lecturerId")
    List<Course> findByLecturerId(@Param("lecturerId") Long lecturerId);
}

