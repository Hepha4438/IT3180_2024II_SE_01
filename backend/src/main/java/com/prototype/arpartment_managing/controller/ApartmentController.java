package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.service.ApartmentResidentService;
import com.prototype.arpartment_managing.service.ApartmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private ApartmentService apartmentService;
    @GetMapping("/apartments")
    List<Apartment> getAllApartments(){
        return apartmentService.getAllApartments();
    }

    @PostMapping("/apartment")
    public ResponseEntity<?> newApartment(@RequestBody Apartment newApartment){
        apartmentService.createApartment(newApartment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Apartment created successfully");

    }

    //Apartment Id (Room's number)
    @GetMapping("/apartment")
    ResponseEntity<?> getApartment(@RequestParam(required = false) String apartmentId) {
        return apartmentService.getApartmentById1(apartmentId);
    }
    // Delete Apartment
    @DeleteMapping("/deleteapartment")
    ResponseEntity<?> deleteApartment(@RequestParam(required = false) String apartmentId){
       apartmentService.deleteApartment(apartmentId);
       return ResponseEntity.status(HttpStatus.OK).body("Apartment delete successfully");
    }

    @PutMapping("/apartment/{apartmentId}")
    public Apartment updateApartment(@RequestBody Apartment newApartment, @PathVariable String apartmentId) {
        return apartmentService.updateApartment(newApartment, apartmentId);
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

    // Remove a resident from an apartment
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

    @PutMapping("/apartment/{apartmentId}/total")
    public ResponseEntity<?> totalRevenueOfApartment(@PathVariable String apartmentId) {
        try {
            Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
            double total = apartmentService.calculateTotalPayment(apartmentId);
            apartment.setTotal(total);
            apartmentRepository.save(apartment);
            return ResponseEntity.ok(apartment);
        } catch (ApartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating total revenue: " + e.getMessage());
        }
    }

    @GetMapping("/apartment/{apartmentID}/{type}")
    public double getFeeByType(@PathVariable String apartmentID, @PathVariable String type) {
        return apartmentService.calculateFeeByType(apartmentID, type);
    }

//    @PostMapping("/apartment/{apartmentId}/{feeType}")
//    public ResponseEntity<?> totalRevenueOfApartmentByType(@PathVariable String apartmentId ,@PathVariable String feeType) {
//        try {
//            Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
//                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
//            double total = apartmentService.calculateFee(apartmentId, feeType);
//            return ResponseEntity.ok(total);
//        } catch (ApartmentNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating total revenue: " + e.getMessage());
//        }
//    }

    
}