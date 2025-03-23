package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    public Optional<Apartment> getApartmentById(String apartmentId) {
        return apartmentRepository.findByApartmentId(apartmentId);
    }

    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

}
