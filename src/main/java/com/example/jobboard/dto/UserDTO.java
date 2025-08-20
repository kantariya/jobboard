package com.example.jobboard.dto;


public class UserDTO {
	private Long id;
	private String username;
	private String email;
	private String role;
	private String location;
	private String jobPreference;

	// Constructor
	public UserDTO(Long id, String username, String email, String role, String location, String jobPreference) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
		this.location = location;
		this.jobPreference = jobPreference;
	}

	// Getters & Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }

	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }

	public String getJobPreference() { return jobPreference; }
	public void setJobPreference(String jobPreference) { this.jobPreference = jobPreference; }
}
