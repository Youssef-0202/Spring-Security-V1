package com.example.security.service.impl;

import com.example.security.entity.Admin;
import com.example.security.repo.AdminRepo;
import com.example.security.service.facade.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author HP
 **/
@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepo adminRepo;

    public AdminServiceImpl(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public Admin getAdminById(Long id) {
        var admin = adminRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Admin not found with id: "+id));
        return admin;
    }
}
