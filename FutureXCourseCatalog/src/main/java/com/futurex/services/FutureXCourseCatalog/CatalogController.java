package com.futurex.services.FutureXCourseCatalog;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CatalogController {

    @Autowired
    private EurekaClient client;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/")
    @CircuitBreaker(name = "CatalogService", fallbackMethod = "fallbackGetCatalogHome")
    public String getCatalogHome() {
        String courseAppMessage = "";
        InstanceInfo instanceInfo = client.getNextServerFromEureka("fx-course-service", false);
        String courseAppURL = instanceInfo.getHomePageUrl();
        courseAppMessage = restTemplate.getForObject(courseAppURL, String.class);

        return ("Welcome to FutureX Course Catalog " + courseAppMessage);
    }

    @RequestMapping("/catalog")
    @CircuitBreaker(name = "CatalogService1", fallbackMethod = "fallbackGetCatalog")
    public String getCatalog() {
        String courses = "";
        InstanceInfo instanceInfo = client.getNextServerFromEureka("fx-course-service", false);
        String courseAppURL = instanceInfo.getHomePageUrl() + "/courses";
        courses = restTemplate.getForObject(courseAppURL, String.class);

        return ("Our courses are " + courses);
    }

    @RequestMapping("/firstcourse")
    @CircuitBreaker(name = "CatalogService2", fallbackMethod = "fallbackGetSpecificCourse")
    public String getSpecificCourse() {
        Course course = new Course();
        InstanceInfo instanceInfo = client.getNextServerFromEureka("fx-course-service", false);
        String courseAppURL = instanceInfo.getHomePageUrl() + "/1";
        course = restTemplate.getForObject(courseAppURL, Course.class);

        return ("Our first course is " + course.getCoursename());
    }

    // Métodos de fallback
    public String fallbackGetCatalogHome(Throwable throwable) {
        // Aquí podrías registrar el error o hacer algo más
        return "Welcome to FutureX Course Catalog. Course information is currently unavailable.";
    }

    public String fallbackGetCatalog(Throwable throwable) {
        // Aquí podrías registrar el error o hacer algo más
        return "Our courses are currently unavailable. Please try again later.";
    }

    public String fallbackGetSpecificCourse(Throwable throwable) {
        return "Our first course information is currently unavailable. Please try again later.";
    }
}
