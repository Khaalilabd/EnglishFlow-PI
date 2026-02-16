package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/published")
    public ResponseEntity<List<CourseDTO>> getPublishedCourses() {
        List<CourseDTO> courses = courseService.getPublishedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/instructor/{instructor}")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(@PathVariable String instructor) {
        List<CourseDTO> courses = courseService.getCoursesByInstructor(instructor);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<CourseDTO>> getCoursesByLevel(@PathVariable String level) {
        List<CourseDTO> courses = courseService.getCoursesByLevel(level);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String query) {
        List<CourseDTO> courses = courseService.searchCourses(query);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}

