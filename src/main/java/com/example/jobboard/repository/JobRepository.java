package com.example.jobboard.repository;


import com.example.jobboard.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {



    List<Job> findByJobType(String jobType); // Find jobs by jobType only

    List<Job> findByJobTypeAndLocation(String jobType, String location); // Find jobs by jobType and exact location






}
