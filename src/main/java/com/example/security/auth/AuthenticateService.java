package com.example.security.auth;

import com.example.security.email.EmailService;
import com.example.security.email.EmailTemplateName;
import com.example.security.entity.Admin;
import com.example.security.entity.Client;
import com.example.security.role.Role;
import com.example.security.role.RoleRepository;
import com.example.security.security.JwtService;
import com.example.security.user.Token;
import com.example.security.user.TokenRepository;
import com.example.security.user.User;
import com.example.security.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticateService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public ResponseEntity<?> register(RegistrationRequest request) throws MessagingException {
        request.validateRoleSpecificFields();


        if (request.isAdmin()) {
            Admin admin = Admin.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .enabled(false)
                    .adminDepartment(request.getAdminDepartment())
                    .adminSecurityLevel(request.getAdminSecurityLevel())
                    .build();

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
            admin.setRoles(List.of(adminRole));
            userRepository.save(admin);
            emailService.sendValidationEmail(admin);
        } else {
            Client client = Client.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .enabled(false)
                    .clientCompany(request.getClientCompany())
                    .clientSubscriptionType(request.getClientSubscriptionType())
                    .build();

            Role clientRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(() -> new IllegalStateException("CLIENT role not found"));
            client.setRoles(List.of(clientRole));
            userRepository.save(client);
            emailService.sendValidationEmail(client);
        }

        Map<String,String> responseMessage = new HashMap<>();
        String role = request.isAdmin() ? "ADMIN " : "CLIENT";
        responseMessage.put("message", STR."Registration successful with role :  \{role} ! ");
        return ResponseEntity.accepted().body(responseMessage);
    }


    @Transactional
    public void deleteAllUsers(){
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }



    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String,Object>();
        var user = ((User) auth.getPrincipal() );
        claims.put("fullName",user.getFullName());
        var jwtToken = jwtService.generateToken(claims,user);
        var userRole = roleRepository.findByName(user.getRoles().get(0).getName()).get();
        // TODO : encode userRole into jwtToken
        // TODO : encode admin/client info in token (now you just sent email-firstname-lastname )
        return AuthenticationResponse.builder().role(userRole.getName()).token(jwtToken).build();
    }

   // @Transactional
    public void activateAccount(String token) throws MessagingException {
    Token savedToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException(("invalid token")));
    if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())){
        emailService.sendValidationEmail(savedToken.getUser());
        throw new RuntimeException("Activation token has expired. A new token has been send !");
    }
    var user = userRepository.findById(savedToken.getUser().getId())
            .orElseThrow(()->new UsernameNotFoundException("user not found"));
    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidateAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
    }

}
