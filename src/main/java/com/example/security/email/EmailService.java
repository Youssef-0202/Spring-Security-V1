package com.example.security.email;

import com.example.security.user.Token;
import com.example.security.user.TokenRepository;
import com.example.security.user.User;
import com.example.security.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSendActivationToken(user);
        try {
            sendEmail(
                    user.getEmail(),
                    user.getFullName(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    activationUrl,
                    newToken,
                    "Account activation"
            );
        }catch (MessagingException e){
            tokenRepository.deleteByUserId(user.getId());
            userRepository.deleteById(user.getId());
            throw new MessagingException("Failed to send validation email to " + user.getEmail() , e);
        }
    }

    private String generateAndSendActivationToken(User user) {
        String generatedToken = generateActiveCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActiveCode(int lenght) {
        String character = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i= 0;i<lenght;i++){
            int randomIndex = secureRandom.nextInt(character.length());
            codeBuilder.append(character.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.name();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        Map<String,Object> proprites = new HashMap<>();
        proprites.put("username",username);
        proprites.put("confirmationUrl",confirmationUrl);
        proprites.put("activation_code",activationCode);

        Context context = new Context();
        context.setVariables(proprites);

        helper.setFrom("youssefaitbahssine23@gmail.com");
        helper.addTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName , context);
        helper.setText(template,true);


        try {
            mailSender.send(mimeMessage);
        }catch (MailException e){
            throw new MessagingException("Failed to send email", e);
        }

    }
}
