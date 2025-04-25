package com.example.security.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Token {
    @Id @GeneratedValue
    private Long id;
    private String token;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;
    private LocalDateTime expiredAt;
    private LocalDateTime validateAt;
    @ManyToOne
    @JoinColumn(
            name = "user_id",nullable = false
    )
    private User  user;
}
