package com.example.security.service.impl;

import com.example.security.entity.Client;
import com.example.security.repo.ClientRepo;
import com.example.security.service.facade.AdminService;
import com.example.security.service.facade.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;

    public ClientServiceImpl(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Override
    public Client getClientById(Long id) {
        var client = clientRepo.findById(id).orElseThrow(()-> new EntityNotFoundException(STR."User not found with id: \{id}"));
        return client;
    }
}
