package com.hritik.auth_service.service;


import com.hritik.auth_service.helpers.AuthPassengerDetails;
import com.hritik.auth_service.repositories.PassengerRepository;
import com.hritik.entity_service.model.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is responsible for loading the user in the form of UserDetails object for auth.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Passenger> passenger = passengerRepository.findPassengerByEmail(email);

        if (passenger.isPresent()) {
            Passenger p = passenger.get();

            // Check if email is verified
            if (!p.getEmailVerified()) {
                throw new RuntimeException("Email not verified. Please check your email and verify your account.");
            }

            return new AuthPassengerDetails(p);
        } else {
            throw new UsernameNotFoundException("Cannot find the Passenger by the given Email");
        }
    }
}