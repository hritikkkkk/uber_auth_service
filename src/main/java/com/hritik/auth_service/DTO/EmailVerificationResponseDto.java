package com.hritik.auth_service.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponseDto {
    private Boolean success;
    private String message;

    private String email;
}