package com.example.jobboard.dto;

public class JobDTO {
    private String title;
    private String description;
    private String location;
    private String jobType;
    private Double salary;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
}