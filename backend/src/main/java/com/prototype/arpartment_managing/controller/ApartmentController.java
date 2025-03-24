package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.service.ApartmentResidentService;
import com.prototype.arpartment_managing.service.ApartmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5000")
public class ApartmentController {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApartmentResidentService apartmentResidentService;
    @Autowired
    private ApartmentService apartmentService;

    /**
     *
     * @param newApartment : new apartment
     * @return: return new apartment from UI
     */
    @PostMapping("/apartment")
    Apartment newApartment(@RequestBody Apartment newApartment){
        return apartmentService.createApartment(newApartment);
    }


    @GetMapping("/apartments")
    List<Apartment> getAllApartments(){
        return apartmentService.getAllApartments();
    }

    //Apartment Id (Room's number)
    @GetMapping("/apartment")
    Optional<Apartment> getApartment(@RequestParam(required = false) String apartmentId) {
        return apartmentService.getApartmentById(apartmentId);
    }
    // Delete Apartment
    @Transactional
    @DeleteMapping("/deleteapartment")
    String deleteApartment(@RequestParam(required = false) String apartmentId){
       return apartmentService.deleteApartment(apartmentId);
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
        return apartmentService.updateApartment(newApartment, apartmentId);
    }

    @GetMapping("/apartment/total")
    public double totalFee(@RequestBody Apartment apartment){


        return 122345;
    }
}