package com.example.jobboard.dto;

// This DTO is used for the update payload.
// it does NOT have a 'role' field, preventing privilege escalation.
public class UserUpdateDTO {

    private String email;
    private String password; // Can be null if not changing
    private String location;
    private String jobPreference;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobPreference() { return jobPreference; }
    public void setJobPreference(String jobPreference) { this.jobPreference = jobPreference; }
}