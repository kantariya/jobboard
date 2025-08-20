package com.example.jobboard.controller;

import com.example.jobboard.entity.Job;
import com.example.jobboard.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Post a new job
    @PostMapping("/post")
    public ResponseEntity<Job> postJob(@RequestBody Job job,@RequestParam Long userId) {
        Job createdJob = jobService.postJob(job,userId);
        return ResponseEntity.ok(createdJob);
    }

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

    // Get jobs by job type
    @GetMapping("/search/type/{jobType}")
    public ResponseEntity<List<Job>> getJobsByType(@PathVariable String jobType) {
        List<Job> jobs = jobService.getJobsByType(jobType);
        return ResponseEntity.ok(jobs);
    }

    // Get jobs by job type and exact location
    @GetMapping("/search/type/{jobType}/location/{location}")
    public ResponseEntity<List<Job>> getJobsByTypeAndLocation(@PathVariable String jobType, @PathVariable String location) {
        List<Job> jobs = jobService.getJobsByTypeAndLocation(jobType, location);
        return ResponseEntity.ok(jobs);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
        Job updatedJob = jobService.updateJob(id, job);
        return ResponseEntity.ok(updatedJob);
    }

    // Delete job by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }


}
