package com.example.jobboard.repository;

import com.example.jobboard.entity.Application;
import com.example.jobboard.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {



    List<Job> findByJobType(String jobType); // Find jobs by jobType only

    List<Job> findByJobTypeAndLocation(String jobType, String location); // Find jobs by jobType and exact location



}
