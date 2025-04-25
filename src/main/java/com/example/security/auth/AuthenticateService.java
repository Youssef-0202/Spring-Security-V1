package com.example.security.auth;

import com.example.security.email.EmailService;
import com.example.security.email.EmailTemplateName;
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
        var userRoles = roleRepository.findByName("USER")
                .orElseThrow(()-> new IllegalStateException("ROLE USER was not initialized"));

        User user = User.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .roles(List.of(userRoles))
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .build();
        userRepository.save(user);
        emailService.sendValidationEmail(user);
        Map<String,String> responseMessage = new HashMap<>();
        responseMessage.put("message","Registration successful with role user ! ");
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
        return AuthenticationResponse.builder().token(jwtToken).build();
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
