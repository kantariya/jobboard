package com.example.jobboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;
    private String password;
    private String role; // "USER" or "RECRUITER"
    private String location;
    private String jobPreference; // Type of job the user is searching for

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference("company-users")
    private Company company; // Only for recruiters

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    @JsonManagedReference("user-jobs")
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    @JsonManagedReference("user-applications")
    private Set<Application> applications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-posts")
    private Set<Post> posts = new HashSet<>();



    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getJobPreference() {
        return jobPreference;
    }

    public void setJobPreference(String jobPreference) {
        this.jobPreference = jobPreference;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public void setApplications(Set<Application> applications) {
        this.applications = applications;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
