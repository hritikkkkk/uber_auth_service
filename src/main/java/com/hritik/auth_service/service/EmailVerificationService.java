package com.hritik.auth_service.service;

import com.hritik.auth_service.repositories.EmailVerificationTokenRepository;
import com.hritik.auth_service.repositories.PassengerRepository;
import com.hritik.entity_service.model.EmailVerificationToken;
import com.hritik.entity_service.model.Passenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final PassengerRepository passengerRepository;
    private final EmailService emailService;

    /**
     * Generate and send verification email
     */
    @Async
    @Transactional
    public void sendVerificationEmail(Passenger passenger) {
        try {
            // Delete any existing tokens for this email
            tokenRepository.deleteByEmail(passenger.getEmail());

            // Generate new verification token
            String token = UUID.randomUUID().toString();

            // Create verification token entity
            EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                    .token(token)
                    .email(passenger.getEmail())
                    .passenger(passenger)
                    .expiresAt(LocalDateTime.now().plusHours(24)) // 24 hours expiry
                    .build();

            tokenRepository.save(verificationToken);

            emailService.sendVerificationEmail(passenger.getEmail(), token);

            log.info("Verification email sent for user: {}", passenger.getEmail());

        } catch (Exception e) {
            log.error("Failed to send verification email for user: {}", passenger.getEmail(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Transactional
    public boolean verifyEmail(String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            log.warn("Verification attempted with invalid token: {}", token);
            return false;
        }

        EmailVerificationToken verificationToken = tokenOpt.get();


        if (verificationToken.isExpired()) {
            log.warn("Verification attempted with expired token for email: {}",
                    verificationToken.getEmail());
            return false;
        }


        if (verificationToken.isVerified()) {
            log.warn("Verification attempted with already used token for email: {}",
                    verificationToken.getEmail());
            return false;
        }

        // Mark token as verified
        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        // Update passenger
        Passenger passenger = verificationToken.getPassenger();
        passenger.setEmailVerified(true);
        passenger.setEnabled(true);
        passengerRepository.save(passenger);


        emailService.sendWelcomeEmail(passenger.getEmail(), passenger.getName());

        log.info("Email verified successfully for user: {}", passenger.getEmail());
        return true;
    }


    @Transactional
    public boolean resendVerificationEmail(String email) {
        Optional<Passenger> passengerOpt = passengerRepository.findPassengerByEmail(email);

        if (passengerOpt.isEmpty()) {
            log.warn("Resend verification attempted for non-existent email: {}", email);
            return false;
        }

        Passenger passenger = passengerOpt.get();

        // Check if already verified
        if (passenger.getEmailVerified()) {
            log.warn("Resend verification attempted for already verified email: {}", email);
            return false;
        }

        sendVerificationEmail(passenger);
        return true;
    }

    /**
     * Clean up expired tokens (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired verification tokens", deletedCount);
        }
    }
}