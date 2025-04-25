package com.example.security.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticateService authenticateService;

    @PostMapping(path = "/register" )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
           @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        return authenticateService.register(request);
    }

    @DeleteMapping("/")
    public void deleteAllUsers(){
        authenticateService.deleteAllUsers();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticateRequest request
    ){
     return ResponseEntity.ok(authenticateService.authenticate(request));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticateService.activateAccount(token);
    }


}
