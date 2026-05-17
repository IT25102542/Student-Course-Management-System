package com.courseRegistration.studentRegistration.repository;

import com.courseRegistration.studentRegistration.model.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface  extends JpaRepository<Enrollment, Long> {
    @Query("select count(e) from Enrollment e where e.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("select e from Enrollment e where e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);

    @Query("select e from Enrollment e where e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

    @Query("select count(e) > 0 from Enrollment e where e.student.id = :studentId and e.course.id = :courseId")
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
