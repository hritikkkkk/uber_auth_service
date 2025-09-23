package com.hritik.auth_service.controller;


import com.hritik.auth_service.DTO.*;
import com.hritik.auth_service.service.AuthService;

import com.hritik.auth_service.service.EmailVerificationService;
import com.hritik.auth_service.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup/passenger")
    public ResponseEntity<?> signUp(@RequestBody PassengerSignupRequestDto passengerSignupRequestDto) {
        try {
            PassengerDTO response = authService.signupPassenger(passengerSignupRequestDto);
            return ResponseEntity.ok(
                    AuthResponseDto.builder()
                            .success(true)
                            .email(response.getEmail())
                            .emailVerified(false)
                            .message("Account created successfully! Please check your email to verify your account.")
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    AuthResponseDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto,
                                    HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDto.getEmail(),
                            authRequestDto.getPassword()
                    )
            );

            String jwtToken = jwtService.createToken(authRequestDto.getEmail());

            ResponseCookie cookie = ResponseCookie.from("JwtToken", jwtToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(11))
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(
                    AuthResponseDto.builder()
                            .success(true)
                            .email(authRequestDto.getEmail())
                            .emailVerified(true)
                            .message("Login successful")
                            .build()
            );

        } catch (Exception ex) {
            String errorMessage = "Invalid credentials";

            if (ex.getMessage().contains("Email not verified")) {
                errorMessage = "Please verify your email before signing in";
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDto.builder()
                            .success(false)
                            .message(errorMessage)
                            .build());
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponseDto> verifyEmail(
            @RequestBody EmailVerificationRequestDto request) {

        boolean verified = emailVerificationService.verifyEmail(request.getToken());
        if (verified) {
            return ResponseEntity.ok(
                    EmailVerificationResponseDto.builder()
                            .success(true)
                            .message("Email verified successfully! You can now sign in.")
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    EmailVerificationResponseDto.builder()
                            .success(false)
                            .message("Invalid or expired verification token")
                            .build()
            );
        }
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<EmailVerificationResponseDto> resendVerification(
            @RequestBody ResendVerificationRequestDto request) {

        boolean sent = emailVerificationService.resendVerificationEmail(request.getEmail());

        if (sent) {
            return ResponseEntity.ok(
                    EmailVerificationResponseDto.builder()
                            .success(true)
                            .email(request.getEmail())
                            .message("Verification email sent successfully")
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    EmailVerificationResponseDto.builder()
                            .success(false)
                            .message("Unable to send verification email. Please check your email address.")
                            .build()
            );
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponseDto> validate(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDto.builder()
                            .success(false)
                            .message("Not authenticated")
                            .build());
        }

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(
                AuthResponseDto.builder()
                        .success(true)
                        .email(user.getUsername())
                        .emailVerified(true)
                        .message("User is authenticated")
                        .build()
        );
    }
}