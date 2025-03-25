package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByType(String type);
    Optional<Fee> findByTypeAndApartment_ApartmentId(String type, String apartmentId);
}
