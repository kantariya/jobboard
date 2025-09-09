package com.example.jobboard.service;

import com.example.jobboard.dto.CompanyDTO;
import com.example.jobboard.entity.Company;
import com.example.jobboard.entity.User;
import com.example.jobboard.exception.InvalidRequestException;
import com.example.jobboard.exception.ResourceNotFoundException;
import com.example.jobboard.repository.CompanyRepository;
import com.example.jobboard.repository.JobRepository;
import com.example.jobboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    // Public methods remain unchanged.
    public List<Company> getAllCompanies() { return companyRepository.findAll(); }
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    // SECURE: Create a company.
    @Transactional
    public Company createCompany(CompanyDTO companyDTO, String recruiterUsername) {
        if (companyRepository.findByName(companyDTO.getName()).isPresent()) {
            throw new InvalidRequestException("Company name already exists");
        }

        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        if (recruiter.getCompany() != null) {
            throw new InvalidRequestException("This recruiter is already associated with a company.");
        }

        Company newCompany = new Company();
        newCompany.setName(companyDTO.getName());
        newCompany.setLocation(companyDTO.getLocation());
        newCompany.setDescription(companyDTO.getDescription());

        // Establish the bidirectional relationship
        recruiter.setCompany(newCompany);
        newCompany.getRecruiters().add(recruiter);

        return companyRepository.save(newCompany);
    }

    // SECURE: Update a company.
    @Transactional
    public Company updateCompany(Long companyId, CompanyDTO companyDTO, String recruiterUsername) {
        Company companyToUpdate = getCompanyById(companyId);
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK: Ensure the recruiter belongs to the company they are trying to update.
        if (recruiter.getCompany() == null || !recruiter.getCompany().getId().equals(companyToUpdate.getId())) {
            throw new AccessDeniedException("You are not authorized to update this company.");
        }

        companyToUpdate.setName(companyDTO.getName());
        companyToUpdate.setDescription(companyDTO.getDescription());
        companyToUpdate.setLocation(companyDTO.getLocation());

        return companyRepository.save(companyToUpdate);
    }

    // SECURE: Delete a company.
    @Transactional
    public void deleteCompany(Long companyId, String recruiterUsername) {
        Company companyToDelete = getCompanyById(companyId);
        User recruiter = userRepository.findByUsername(recruiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // AUTHORIZATION CHECK: Ensure the recruiter belongs to the company they are trying to delete.
        if (recruiter.getCompany() == null || !recruiter.getCompany().getId().equals(companyToDelete.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this company.");
        }

        // Before deleting a company, we must unlink all recruiters from it.
        for (User user : companyToDelete.getRecruiters()) {
            user.setCompany(null);
            userRepository.save(user);
        }

        companyRepository.delete(companyToDelete);
    }
}