package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.FeeNotFoundException;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.FeeRepository;
import com.prototype.arpartment_managing.repository.RevenueRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Apartment> getAllApartments() {

        return apartmentRepository.findAll();
    }

//    public Optional<Apartment> getApartmentById(String apartmentId) {
//        if (apartmentId != null) {
//            return Optional.ofNullable(apartmentRepository.findByApartmentId(apartmentId)
//                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId)));
//        } else {
//            throw new IllegalArgumentException("Must provide either apartment room's number");
//        }
//    }
    public ResponseEntity<?> getApartmentById1(String apartmentId){
        if (apartmentId != null) {
            Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        } else {
            return ResponseEntity.badRequest().body("Must provide either username or id");
        }
        return ResponseEntity.ok(new Apartment(apartmentId));
    }

    public void createApartment(Apartment apartment) {

        apartmentRepository.save(apartment);
    }

    @Transactional
    public void deleteApartment(String apartmentId) {
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

        for (User resident : apartment.getResidents()) {
            resident.setApartment(null);
        }
        userRepository.saveAll(apartment.getResidents()); // Save changes to users

        revenueRepository.deleteAll(new ArrayList<>(apartment.getRevenues()));
        apartmentRepository.delete(apartment);
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
                    apartment.setTotal(newApartment.getTotal());
                    return apartmentRepository.save(apartment);
                }).orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
    }
    public Double calculateFeeByType(String apartmentId, String feeType) {
        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);
        if (revenues.isEmpty()) {
            return 0.0;
        }
        Fee fee = feeRepository.findByType(feeType)
                .orElseThrow(() -> new IllegalArgumentException("Fee type not found: " + feeType));
        return revenues.stream()
                .filter(revenue -> feeType.equals(revenue.getType())) // Selects only revenues matching the requested feeType
                .mapToDouble(revenue -> revenue.getUsed() * fee.getPricePerUnit()) // Calculates individual fees
                .sum(); // Sums up all fees
    }
    public List<Revenue> findAllRevenueByApartmentId(String apartmentId) {
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

        return apartment.getRevenues();
    }

    public Double calculateTotalPayment(String apartmentId) {
        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);
        if (revenues.isEmpty()) {
            return 0.0;
        }

        Set<String> feeTypes = revenues.stream()
                .map(Revenue::getType)
                .collect(Collectors.toSet());

        Map<String, Double> feePriceMap = feeRepository.findByTypeIn(feeTypes).stream()
                .collect(Collectors.toMap(Fee::getType, Fee::getPricePerUnit));

        return revenues.stream()
                .mapToDouble(revenue -> {
                    Double pricePerUnit = feePriceMap.get(revenue.getType());
                    if (pricePerUnit == null) {
                        throw new FeeNotFoundException("Fee not found for type: " + revenue.getType());
                    }
                    return revenue.getUsed() * pricePerUnit;
                })
                .sum();
    }



}