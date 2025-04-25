package com.example.security.controller;

import com.example.security.entity.Admin;
import com.example.security.service.facade.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HP
 **/

@RestController
@RequestMapping("client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientByID(@PathVariable Long id){
        var admin = clientService.getClientById(id);
        return ResponseEntity.ok(admin);
    }
}
