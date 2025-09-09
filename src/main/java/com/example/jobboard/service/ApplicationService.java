package com.example.jobboard.service;

import com.example.jobboard.dto.ApplicationDTO;
import com.example.jobboard.dto.ApplicationStatusDTO;
import com.example.jobboard.entity.Application;
import com.example.jobboard.entity.Job;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.InvalidRequestException;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.ApplicationRepository;
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
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;

    // --- APPLICANT ACTIONS (Unchanged) ---

    @Transactional
    public Application applyForJob(Long jobId, ApplicationDTO applicationDTO, String applicantUsername) {
        User applicant = userRepository.findByUsername(applicantUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.existsByApplicantAndJob(applicant, job)) {
            throw new InvalidRequestException("You have already applied for this job.");
        }

        Application newApplication = new Application();
        newApplication.setApplicant(applicant);
        newApplication.setJob(job);
        newApplication.setResume(applicationDTO.getResume());
        newApplication.setStatus("PENDING");
        newApplication.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(newApplication);
    }

    public List<Application> getApplicationsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return applicationRepository.findByApplicantId(user.getId());
    }

    public void withdrawApplication(Long applicationId, String applicantUsername) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        User applicant = userRepository.findByUsername(applicantUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));

        if (!Objects.equals(application.getApplicant().getId(), applicant.getId())) {
            throw new AccessDeniedException("You are not authorized to withdraw this application.");
        }
        applicationRepository.delete(application);
    }

    // --- RECRUITER ACTIONS ---

    public List<Application> getApplicationsByJobForRecruiter(Long jobId, String recruiterUsername) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK 1: Ensure the user has the RECRUITER role.
        if (!"ROLE_RECRUITER".equals(recruiter.getRole())) {
            throw new AccessDeniedException("You must be a recruiter to perform this action.");
        }

        // AUTHORIZATION CHECK 2: Ensure the recruiter posted the job.
        if (!Objects.equals(job.getPostedBy().getId(), recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to view applications for this job.");
        }
        return applicationRepository.findByJobId(jobId);
    }

    @Transactional
    public Application updateApplicationStatus(Long applicationId, ApplicationStatusDTO statusDTO, String recruiterUsername) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // UTHORIZATION CHECK 1: Ensure the user has the RECRUITER role.
        if (!"ROLE_RECRUITER".equals(recruiter.getRole())) {
            throw new AccessDeniedException("You must be a recruiter to perform this action.");
        }

        // AUTHORIZATION CHECK 2: Ensure the recruiter owns the job the application is for.
        if (!Objects.equals(application.getJob().getPostedBy().getId(), recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to update this application.");
        }

        application.setStatus(statusDTO.getStatus());
        return applicationRepository.save(application);
    }
}