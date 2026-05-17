package com.courseRegistration.studentRegistration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String subjects;

    private String duration;

    private int capacity;

    @Column(nullable = false)
    private BigDecimal price;

    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_lecturers",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "lecturer_id")
    )
    @JsonIgnoreProperties("courses")
    private Set<Lecturer> lecturers = new LinkedHashSet<>();

    @Transient
    private List<Long> lecturerIds = new ArrayList<>();

    public Course() {
    }

    public Course(String courseCode, String title, String description, String subjects, String duration,
                  int capacity, BigDecimal price) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.subjects = subjects;
        this.duration = duration;
        this.capacity = capacity;
        this.price = price;
        this.active = true;
    }

}
