package com.example.jobboard.service;

import com.example.jobboard.entity.Company;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.CompanyRepository;
import com.example.jobboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all companies
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // Get company by ID
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    // Create a new company (Corrected: Takes ONLY `Company company`)
    @Transactional
    public Company createCompany(Company company, Long recruiterId) {
        // Check if the company name already exists
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new RuntimeException("Company name already exists");
        }

        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        if (!"ROLE_RECRUITER".equals(recruiter.getRole())) {
            throw new RuntimeException("User is not a recruiter");
        }

        recruiter.setCompany(company); // Assign company to recruiter
        company.getRecruiters().add(recruiter); // Add recruiter to company's recruiter list


        return companyRepository.save(company);
    }


    // Update a company (Corrected: Takes `companyId` + `updatedCompany`)
    public Company updateCompany(Long companyId, Company updatedCompany) {
        Company company = getCompanyById(companyId);

        if (updatedCompany.getName() != null) {
            company.setName(updatedCompany.getName());
        }
        if (updatedCompany.getDescription() != null) {
            company.setDescription(updatedCompany.getDescription());
        }
        if (updatedCompany.getLocation() != null) {
            company.setLocation(updatedCompany.getLocation());
        }

        if (company.getName() == null || company.getDescription() == null || company.getLocation() == null) {
            throw new IllegalArgumentException("Company name, description, and location cannot be null.");
        }

        return companyRepository.save(company);
    }


    // Delete a company
    public void deleteCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }

        companyRepository.deleteById(companyId);
    }
}
