package com.prototype.arpartment_managing.repository;

import com.prototype.arpartment_managing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
