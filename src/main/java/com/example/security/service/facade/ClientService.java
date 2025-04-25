package com.example.security.service.facade;

import com.example.security.entity.Admin;
import com.example.security.entity.Client;
import com.example.security.repo.AdminRepo;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/

public interface ClientService {

    Client getClientById(Long id);
}
