package com.hritik.auth_service.repositories;

import com.hritik.entity_service.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
