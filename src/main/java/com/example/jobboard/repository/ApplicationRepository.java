package com.example.jobboard.repository;

import com.example.jobboard.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantId(Long applicantId); // Get applications submitted by a user

    List<Application> findByJobId(Long jobId); // Get applications for a specific job

}
