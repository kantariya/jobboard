package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.dto.JobDTO;
import com.example.jobboard.entity.Job;
import com.example.jobboard.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs") // Using /api prefix
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // --- SECURE ACTIONS ---

    // SECURE: Post a new job. User is taken from authentication context.
    @PostMapping
    public ResponseEntity<Job> postJob(
            @RequestBody JobDTO jobDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Job createdJob = jobService.postJob(jobDTO, currentUser.getUsername());
        return ResponseEntity.ok(createdJob);
    }

    // SECURE: Update a job, with ownership validation.
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable Long id,
            @RequestBody JobDTO jobDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Job updatedJob = jobService.updateJob(id, jobDTO, currentUser.getUsername());
        return ResponseEntity.ok(updatedJob);
    }

    // SECURE: Delete a job, with ownership validation.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        jobService.deleteJob(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    // --- PUBLIC ACTIONS ---

    // Get a job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    // Search jobs by type
    @GetMapping("/search/type/{jobType}")
    public ResponseEntity<List<Job>> getJobsByType(@PathVariable String jobType) {
        List<Job> jobs = jobService.getJobsByType(jobType);
        return ResponseEntity.ok(jobs);
    }

    // Search jobs by type and location
    @GetMapping("/search/type/{jobType}/location/{location}")
    public ResponseEntity<List<Job>> getJobsByTypeAndLocation(@PathVariable String jobType, @PathVariable String location) {
        List<Job> jobs = jobService.getJobsByTypeAndLocation(jobType, location);
        return ResponseEntity.ok(jobs);
    }
}