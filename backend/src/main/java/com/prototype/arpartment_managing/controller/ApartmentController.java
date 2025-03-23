package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.service.ApartmentResidentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:5000")
public class ApartmentController {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApartmentResidentService apartmentResidentService;
    @PostMapping("/apartment")
    Apartment newApartment(@RequestBody Apartment newApartment){
        return apartmentRepository.save(newApartment);
    }
    @GetMapping("/apartments")
    List<Apartment> getAllApartments(){
        return apartmentRepository.findAll();
    }

    //Apartment Id (Room's number)
    @GetMapping("/apartment")
    Apartment getApartment(@RequestParam(required = false) String apartmentId) {
        if (apartmentId != null) {
            return apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        } else {
            throw new IllegalArgumentException("Must provide either apartment room's number");
        }
    }
    // Delete Apartment
    @Transactional
    @DeleteMapping("/deleteapartment")
    String deleteApartment(@RequestParam(required = false) String apartmentId){
        // Check if apartment exists
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

        // Unassign all residents from this apartment
        List<User> residents = apartment.getResidents();
        for (User resident : residents) {
            resident.setApartment(null); // Break the relationship
        }

        userRepository.saveAll(residents); // Persist the updated users
        apartmentRepository.deleteByApartmentId(apartmentId);
        return "Apartment with number "+apartmentId+" has been deleted sucessfully";
    }
    // Add resident to apartment
    @PutMapping("/apartment/add-resident/{apartmentId}")
    public ResponseEntity<?> addResidentToApartment(@PathVariable String apartmentId, @RequestParam Long userId) {
        try {
            apartmentResidentService.addResidentToApartment(userId, apartmentId);
            return ResponseEntity.ok("User successfully added to apartment " + apartmentId);
        } catch (ApartmentNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add resident to apartment: " + e.getMessage());
        }
    }

    //Remove a resident from an apartment
    @PutMapping("/apartment/remove-resident/{apartmentId}")
    public ResponseEntity<?> removeResidentFromApartment(@PathVariable String apartmentId, @RequestParam Long userId) {
        try {
            apartmentResidentService.removeResidentFromApartment(userId, apartmentId);
            return ResponseEntity.ok("User successfully removed from apartment " + apartmentId);
        } catch (ApartmentNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove resident from apartment: " + e.getMessage());
        }
    }

    @PutMapping("/apartment/{apartmentId}")
    public Apartment updateApartment(@RequestBody Apartment newApartment, @PathVariable String apartmentId) {
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
