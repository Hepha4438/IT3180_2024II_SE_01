package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    Optional<Fee> findByType(String type);

}
