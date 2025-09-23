package com.hritik.auth_service.repositories;

import com.hritik.entity_service.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.email = :email")
    void deleteByEmail(@Param("email") String email);
}