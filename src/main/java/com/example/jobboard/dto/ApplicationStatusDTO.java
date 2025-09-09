package com.example.jobboard.dto;

// DTO for the recruiter's payload when updating an application's status
public class ApplicationStatusDTO {
    private String status; // e.g., "ACCEPTED", "REJECTED"

    // Getter and Setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}