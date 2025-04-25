package com.example.security.controller;

import com.example.security.entity.Admin;
import com.example.security.service.facade.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HP
 **/

@RestController
@RequestMapping("admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminByID(@PathVariable Long id){
        var admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }
}
