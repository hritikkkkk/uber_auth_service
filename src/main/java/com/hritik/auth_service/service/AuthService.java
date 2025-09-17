package com.hritik.auth_service.service;

import com.hritik.auth_service.DTO.PassengerDTO;
import com.hritik.auth_service.DTO.PassengerSignupRequestDto;
import com.hritik.auth_service.repositories.PassengerRepository;
import com.hritik.entity_service.model.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    public PassengerDTO signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(passwordEncoder.encode(passengerSignupRequestDto.getPassword())) // TODO: Encrypt the password
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .build();

        Passenger newPassenger = passengerRepository.save(passenger);

        return PassengerDTO.from(newPassenger);
    }
}
