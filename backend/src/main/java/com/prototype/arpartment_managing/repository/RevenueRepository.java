package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    Optional<Revenue> findById(Long id);

    List<Revenue> findByApartment_ApartmentId(String apartmentId);
}
