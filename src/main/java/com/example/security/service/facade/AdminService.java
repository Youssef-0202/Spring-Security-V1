package com.example.security.service.facade;

import com.example.security.entity.Admin;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/


public interface AdminService {

     Admin getAdminById(Long id);
}
