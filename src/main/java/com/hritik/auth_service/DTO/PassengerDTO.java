package com.hritik.auth_service.DTO;

import com.hritik.entity_service.model.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {
    private String id;

    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private Date createdAt;

    public static PassengerDTO from(Passenger p) {
        return PassengerDTO.builder()
                .id(p.getId().toString())
                .createdAt(p.getCreatedAt())
                .email(p.getEmail())
                .password(p.getPassword())
                .phoneNumber(p.getPhoneNumber())
                .name(p.getName())
                .build();
    }
}
