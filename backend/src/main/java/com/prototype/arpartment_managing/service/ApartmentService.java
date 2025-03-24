package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
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
        if (apartmentId != null) {
            return Optional.ofNullable(apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId)));
        } else {
            throw new IllegalArgumentException("Must provide either apartment room's number");
        }
    }

    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public String deleteApartment(String apartmentId) {
        // Check if apartment exists
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        for (User resident : apartment.getResidents()) {
            resident.setApartment(null);
        }
        apartmentRepository.deleteByApartmentId(apartmentId);
        return "Apartment with number "+apartmentId+" has been deleted sucessfully";
    }

    public Apartment updateApartment(Apartment newApartment, String apartmentId) {
        return apartmentRepository.findByApartmentId(apartmentId)
                .map(apartment -> {
                    apartment.setFloor(newApartment.getFloor());
                    apartment.setIsOccupied(newApartment.getIsOccupied());
                    apartment.setOccupants(newApartment.getOccupants());
                    apartment.setOwner(newApartment.getOwner());
                    apartment.setArea(newApartment.getArea());
                    apartment.setApartmentType(newApartment.getApartmentType());
                    return apartmentRepository.save(apartment);
                }).orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
    }



}