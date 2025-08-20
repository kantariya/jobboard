package com.example.jobboard.service;

import com.example.jobboard.entity.Application;
import com.example.jobboard.entity.Job;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ApplicationRepository;
import com.example.jobboard.repository.JobRepository;
import com.example.jobboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    // Apply for a job (Corrected: Takes ONLY `Application application`)
    public Application applyForJob(Application application,Long applicant_id,Long jobId) {

        User applicant = userRepository.findById(applicant_id)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        Job job = jobRepository.findById(jobId) .orElseThrow(() -> new RuntimeException("Job not found"));


        applicant.getApplications().add(application) ;
        application.setApplicant(applicant);
        application.setStatus("PENDING");
        application.setAppliedAt(LocalDateTime.now());
        application.setJob(job);


        return applicationRepository.save(application);
    }

    // Get application by ID
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
    }

    // Get all applications for a specific user
    public List<Application> getApplicationsByUser(Long userId) {
        return applicationRepository.findByApplicantId(userId);
    }

    // Get all applications for a specific job
    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }



    @Transactional
    public Application updateApplication(Long id, Application updatedApplication) {
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Only update fields that are provided (not null)
        if (updatedApplication.getStatus() != null) {
            existingApplication.setStatus(updatedApplication.getStatus());
        }
        if (updatedApplication.getResume() != null) {
            existingApplication.setResume(updatedApplication.getResume());
        }

        return applicationRepository.save(existingApplication);
    }

    // Delete application
    public void deleteApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException("Application not found with ID: " + applicationId);
        }
        applicationRepository.deleteById(applicationId);
    }
}
