package com.futurex.services.FutureXCourseCatalog;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CatalogController {
    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    @Value("${course.service.url}")
    private String courseServiceUrl;
    private final RestTemplate restTemplate;
    private final Tracer tracer;
    private final MeterRegistry meterRegistry;

    @Autowired
    public CatalogController(RestTemplate restTemplate, Tracer tracer, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.tracer = tracer;
        this.meterRegistry = meterRegistry;
    }

    @RequestMapping("/")
    public String getCatalogHome() {
        logger.info("Received request for catalog home");
        Timer.Sample timer = Timer.start(meterRegistry);
        Span span = tracer.spanBuilder("getCatalogHome").startSpan();
        try {
            String courseAppMessage = restTemplate.getForObject(courseServiceUrl, String.class);
            String response = "Welcome to FutureX Course Catalog " + courseAppMessage;
            logger.info("Returning catalog home response");
            return response;
        } catch (Exception e) {
            logger.error("Error in getCatalogHome from service", e);
            throw e;
        } finally {
            span.end();
            timer.stop(meterRegistry.timer("catalog.home.request"));
        }
    }

    @RequestMapping("/catalog")
    public String getCatalog() {
        logger.info("Received request for course catalog");
        Timer.Sample timer = Timer.start(meterRegistry);
        Span span = tracer.spanBuilder("getCatalog").startSpan();
        try {
            String courses = restTemplate.getForObject(courseServiceUrl + "/courses", String.class);
            String response = "Our courses are " + courses;
            logger.info("Returning course catalog");
            return response;
        } catch (Exception e) {
            logger.error("Error in getCatalog", e);
            throw e;
        } finally {
            span.end();
            timer.stop(meterRegistry.timer("catalog.courses.request"));
        }
    }

    @RequestMapping("/firstcourse")
    public String getSpecificCourse() {
        logger.info("Received request for first course");
        Timer.Sample timer = Timer.start(meterRegistry);
        Span span = tracer.spanBuilder("getSpecificCourse").startSpan();
        try {
            Course course = restTemplate.getForObject(courseServiceUrl + "/1", Course.class);
            String response = "Our first course is " + course.getCoursename();
            logger.info("Returning first course information");
            return response;
        } catch (Exception e) {
            logger.error("Error in getSpecificCourse", e);
            throw e;
        } finally {
            span.end();
            timer.stop(meterRegistry.timer("catalog.firstcourse.request"));
        }
    }
}