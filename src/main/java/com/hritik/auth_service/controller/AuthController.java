package com.hritik.auth_service.controller;


import com.hritik.auth_service.DTO.AuthRequestDto;
import com.hritik.auth_service.DTO.AuthResponseDto;
import com.hritik.auth_service.DTO.PassengerDTO;
import com.hritik.auth_service.DTO.PassengerSignupRequestDto;
import com.hritik.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;


    @PostMapping("/signup/passenger")
    public ResponseEntity<PassengerDTO> signUp(@RequestBody PassengerSignupRequestDto passengerSignupRequestDto) {
        PassengerDTO response = authService.signupPassenger(passengerSignupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDto.getEmail(),
                            authRequestDto.getPassword()
                    )
            );


            return ResponseEntity.ok(
                    AuthResponseDto.builder().success(true).build()
            );

        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDto.builder()
                            .success(false)
                            .build());
        }
    }


}