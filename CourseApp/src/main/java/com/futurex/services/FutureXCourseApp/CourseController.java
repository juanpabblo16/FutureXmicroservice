package com.futurex.services.FutureXCourseApp;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final MeterRegistry meterRegistry;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private Tracer tracer;

    public CourseController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @RequestMapping("/")
    public String getCourseAppHome() {
        Span span = tracer.spanBuilder("getCourseAppHome").startSpan();
        try {
            return "Course App Home";
        } finally {
            span.end();
        }
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        logger.info("Fetching all courses");
        meterRegistry.counter("courses.accessed").increment();
        List<Course> courses = courseRepository.findAll();
        logger.info("Fetched {} courses", courses.size());
        return courses;
    }

    @RequestMapping("/{id}")
    public Optional<Course> getSpecificCourse(@PathVariable("id") BigInteger courseId) {
        return courseRepository.findById(courseId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/courses")
    public void saveCourse(@RequestBody Course course) {
        courseRepository.save(course);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    public void deleteCourse(@PathVariable BigInteger id) {
        courseRepository.deleteById(id);
    }
}   