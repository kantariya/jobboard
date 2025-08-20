package com.example.jobboard.controller;

import com.example.jobboard.entity.Application;
import com.example.jobboard.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Apply for a job
    @PostMapping("/apply")
    public ResponseEntity<Application> applyForJob(@RequestBody Application application,@RequestParam Long applicantId,@RequestParam Long jobId) {
        Application createdApplication = applicationService.applyForJob(application,applicantId,jobId);
        return ResponseEntity.ok(createdApplication);
    }


    // Get an application by ID
    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    // Get all applications for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getApplicationsByUser(@PathVariable Long userId) {
        List<Application> applications = applicationService.getApplicationsByUser(userId);
        return ResponseEntity.ok(applications);
    }

    // Get all applications for a specific job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(applications);
    }



    // Update application details (only update fields available in request)
    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id, @RequestBody Application updatedApplication) {
        Application application = applicationService.updateApplication(id, updatedApplication);
        return ResponseEntity.ok(application);
    }


    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.noContent().build();
    }
}
