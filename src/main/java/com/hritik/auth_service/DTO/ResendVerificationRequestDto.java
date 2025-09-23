package com.hritik.auth_service.DTO;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationRequestDto {
    private String email;
}