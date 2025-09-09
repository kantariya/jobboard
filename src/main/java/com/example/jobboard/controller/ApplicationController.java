package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.dto.ApplicationDTO;
import com.example.jobboard.dto.ApplicationStatusDTO;
import com.example.jobboard.entity.Application;
import com.example.jobboard.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Central API prefix
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // --- APPLICANT ACTIONS ---

    // SECURE: An authenticated user applies for a specific job.
    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<Application> applyForJob(
            @PathVariable Long jobId,
            @RequestBody ApplicationDTO applicationDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Application newApplication = applicationService.applyForJob(jobId, applicationDTO, currentUser.getUsername());
        return ResponseEntity.ok(newApplication);
    }

    // SECURE: An authenticated user gets a list of their own applications.
    @GetMapping("/applications/my-applications")
    public ResponseEntity<List<Application>> getMyApplications(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Application> applications = applicationService.getApplicationsByUser(currentUser.getUsername());
        return ResponseEntity.ok(applications);
    }

    // SECURE: An authenticated user withdraws their own application.
    @DeleteMapping("/applications/{applicationId}/withdraw")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        applicationService.withdrawApplication(applicationId, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    // --- RECRUITER ACTIONS ---

    // SECURE: A recruiter gets all applications for a job they posted.
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Application> applications = applicationService.getApplicationsByJobForRecruiter(jobId, currentUser.getUsername());
        return ResponseEntity.ok(applications);
    }

    // SECURE: A recruiter updates the status of an application for one of their jobs.
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusDTO statusDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Application application = applicationService.updateApplicationStatus(applicationId, statusDTO, currentUser.getUsername());
        return ResponseEntity.ok(application);
    }
}