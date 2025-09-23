package com.hritik.auth_service.service;

import com.hritik.auth_service.DTO.PassengerDTO;
import com.hritik.auth_service.DTO.PassengerSignupRequestDto;
import com.hritik.auth_service.repositories.PassengerRepository;
import com.hritik.entity_service.model.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public PassengerDTO signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {

        Optional<Passenger> existingPassenger = passengerRepository
                .findPassengerByEmail(passengerSignupRequestDto.getEmail());

        if (existingPassenger.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(passwordEncoder.encode(passengerSignupRequestDto.getPassword()))
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .emailVerified(false)
                .enabled(false)
                .build();

        Passenger newPassenger = passengerRepository.save(passenger);

        emailVerificationService.sendVerificationEmail(newPassenger);

        return PassengerDTO.from(newPassenger);
    }
}
