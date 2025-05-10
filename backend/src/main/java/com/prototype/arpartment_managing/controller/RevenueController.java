package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.dto.RevenueDTO;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.service.RevenueService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5000")
@RequestMapping("/revenue")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    // Get all revenues - Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueDTO> getAllRevenues() {
        return revenueService.getAllRevenues();
    }

//    @GetMapping("/revenue")
//    public List<Revenue> getRevenueByApartmentId(@RequestParam(required = false) String apartmentId) {
//        return revenueService.findAllRevenueByApartmentId(apartmentId);
//    }

    // Create revenue - Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Revenue createRevenue(@RequestBody RevenueDTO revenueDTO) {
        return revenueService.createRevenue(revenueDTO);
    }

    // Get Revenue for testing - Admin only
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> getRevenue(@RequestParam(required = false) Long id) {
        return revenueService.getRevenue(id);
    }

    // Get Revenue by apartment and type - Admin or resident of the apartment
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isResidentOfApartment(#apartmentId)")
    ResponseEntity<?> getRevenueByApartmentandType(
            @RequestParam(required = true) String apartmentId,
            @RequestParam(required = true) String type) {
        return revenueService.getRevenueByApartmentandType(apartmentId, type);
    }

    // Delete revenue - Admin only
    @Transactional
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> deleteRevenueByID(@RequestParam(required = false) Long id) {
        revenueService.deleteRevenue(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Revenue delete successfully");
    }
//    // xong 1 khoan thu => xoa => can status ??
//    @DeleteMapping("/deleterevenue")
//    public String deleteRevenue(@RequestBody Revenue revenue) {
//        revenueService.deleteRevenue(revenue.getId());
//        return "Deleted Revenue";
//    }

    // Update revenue - Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Revenue updateRevenue(@RequestBody RevenueDTO revenueDTO, @PathVariable Long id) {
        return revenueService.updateRevenueByID(revenueDTO, id);
    }

    // Get revenues by apartment ID - Admin or resident of the apartment
    @GetMapping("/{apartmentId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isResidentOfApartment(#apartmentId)")
    public List<Revenue> getRevenuesByApartmentID(@PathVariable String apartmentId) {
        return revenueService.getRevenueByApartmentId(apartmentId);
    }

//    // tinh tien 1 khoan thu
//    @GetMapping("/revenue/{apartment_id}/{fee}")
//    public double getRevenueFee(@PathVariable String apartment_id, @PathVariable String fee) {
//        return revenueService.calculateFee(apartment_id, fee);
//    }
//
//    //tinh tong khoan thu
//    @GetMapping("/revenue/total/{apartmentId}")
//    public double getTotalRevenue(@PathVariable String apartmentId) {
//        return revenueService.calculateTotalPayment(apartmentId);
//    }
}
