package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
        Optional<User> findByUsername(String username);
        Optional<User> findByEmail(String email);
        Optional<User> findByCitizenIdentification(String citizenIdentification);
}
