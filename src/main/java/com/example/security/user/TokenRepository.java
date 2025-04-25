package com.example.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByToken(String tocken);
    void deleteByUserId(Long id);
    Token findByUserId(Long id);
}
