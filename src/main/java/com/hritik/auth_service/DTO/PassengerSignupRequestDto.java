package com.hritik.auth_service.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerSignupRequestDto {

    private String email;

    private String password;

    private String phoneNumber;

    private String name;


}