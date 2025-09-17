package com.hritik.auth_service.controller;


import com.hritik.auth_service.DTO.PassengerDTO;
import com.hritik.auth_service.DTO.PassengerSignupRequestDto;
import com.hritik.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup/passenger")
    public ResponseEntity<PassengerDTO> signUp(@RequestBody PassengerSignupRequestDto passengerSignupRequestDto) {
        PassengerDTO response = authService.signupPassenger(passengerSignupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}