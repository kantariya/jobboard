package com.example.jobboard.service;

import com.example.jobboard.dto.JobDTO;
import com.example.jobboard.entity.Job;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.InvalidRequestException;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.JobRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    // SECURE: Post a new job
    @Transactional
    public Job postJob(JobDTO jobDTO, String recruiterUsername) {
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK 1: Ensure user has the RECRUITER role.
        if (!"ROLE_RECRUITER".equals(recruiter.getRole())) {
            throw new AccessDeniedException("Only users with the RECRUITER role can post jobs.");
        }

        // AUTHORIZATION CHECK 2: Ensure the recruiter is associated with a company.
        if (recruiter.getCompany() == null) {
            throw new InvalidRequestException("Recruiters must be associated with a company to post a job.");
        }

        Job newJob = new Job();
        newJob.setTitle(jobDTO.getTitle());
        newJob.setDescription(jobDTO.getDescription());
        newJob.setLocation(jobDTO.getLocation());
        newJob.setJobType(jobDTO.getJobType());
        newJob.setSalary(jobDTO.getSalary());
        newJob.setPostedAt(LocalDateTime.now());
        newJob.setPostedBy(recruiter); // Set the authenticated user as the poster

        return jobRepository.save(newJob);
    }

    // SECURE: Update a job
    @Transactional
    public Job updateJob(Long id, JobDTO jobDTO, String recruiterUsername) {
        Job existingJob = getJobById(id);
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK: Ensure the user updating the job is the one who posted it.
        if (!Objects.equals(existingJob.getPostedBy().getId(), recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to update this job posting.");
        }

        existingJob.setTitle(jobDTO.getTitle());
        existingJob.setDescription(jobDTO.getDescription());
        existingJob.setLocation(jobDTO.getLocation());
        existingJob.setJobType(jobDTO.getJobType());
        existingJob.setSalary(jobDTO.getSalary());

        return jobRepository.save(existingJob);
    }

    // SECURE: Delete a job
    public void deleteJob(Long id, String recruiterUsername) {
        Job jobToDelete = getJobById(id);
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK: Ensure the user deleting the job is the one who posted it.
        if (!Objects.equals(jobToDelete.getPostedBy().getId(), recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this job posting.");
        }

        jobRepository.delete(jobToDelete);
    }

    // --- Public methods remain the same ---

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByType(String jobType) {
        return jobRepository.findByJobType(jobType);
    }

    public List<Job> getJobsByTypeAndLocation(String jobType, String location) {
        return jobRepository.findByJobTypeAndLocation(jobType, location);
    }
}