package com.example.jobboard.controller;

import com.example.jobboard.Security.CustomUserDetails;
import com.example.jobboard.dto.CompanyDTO;
import com.example.jobboard.entity.Company;
import com.example.jobboard.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies") // Using /api prefix for consistency
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // PUBLIC: Get all companies (No changes needed)
    @GetMapping("/public")
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    // PUBLIC: Get a company by ID (No changes needed)
    @GetMapping("/public/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    // SECURE: Create a new company, assigned to the authenticated recruiter.
    @PostMapping
    public ResponseEntity<Company> createCompany(
            @RequestBody CompanyDTO companyDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Company newCompany = companyService.createCompany(companyDTO, currentUser.getUsername());
        return ResponseEntity.ok(newCompany);
    }

    // SECURE: Update an existing company, with ownership validation.
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDTO companyDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Company company = companyService.updateCompany(id, companyDTO, currentUser.getUsername());
        return ResponseEntity.ok(company);
    }

    // SECURE: Delete a company, with ownership validation.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        companyService.deleteCompany(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }
}