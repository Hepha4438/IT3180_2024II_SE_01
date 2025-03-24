package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    Optional<Fee> findById(String id);
    Optional<Fee> findByApartmentId(String apartmentId);
    Optional<Fee> findByType(String type);
    Optional<Fee> findByTypeAndApartmentId(String type, String apartmentId);
}
