package com.example.jobboard.service;

import com.example.jobboard.entity.Job;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.JobRepository;
import com.example.jobboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    // Post a new job
    public Job postJob(Job job, Long user_id) {

        User posted_by = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        posted_by.getJobs().add(job);
        job.setPostedBy(posted_by);
        job.setPostedAt(LocalDateTime.now());


        return jobRepository.save(job);
    }

    // Get a job by ID
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get jobs by job type
    public List<Job> getJobsByType(String jobType) {
        return jobRepository.findByJobType(jobType);
    }

    // Get jobs by job type and exact location
    public List<Job> getJobsByTypeAndLocation(String jobType, String location) {
        return jobRepository.findByJobTypeAndLocation(jobType, location);
    }

    @Transactional
    public Job updateJob(Long id, Job updatedJob) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Update only provided fields (not null)
        if (updatedJob.getTitle() != null) {
            existingJob.setTitle(updatedJob.getTitle());
        }
        if (updatedJob.getDescription() != null) {
            existingJob.setDescription(updatedJob.getDescription());
        }
        if (updatedJob.getLocation() != null) {
            existingJob.setLocation(updatedJob.getLocation());
        }
        if (updatedJob.getSalary() != null) {
            existingJob.setSalary(updatedJob.getSalary());
        }if (updatedJob.getJobType() != null) {
            existingJob.setJobType(updatedJob.getJobType());
        }

        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found");
        }
        jobRepository.deleteById(id);
    }
}
