package com.hritik.auth_service.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    public Boolean success;

    public String email;
}