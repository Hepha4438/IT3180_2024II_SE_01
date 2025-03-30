package com.prototype.arpartment_managing.controller;


import com.prototype.arpartment_managing.dto.RevenueDTO;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.service.RevenueService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("https://it3180se01sprint1-9ecjvnlvb-hephas-projects.vercel.app/")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/revenues")
    public List<Revenue> getAllRevenues() {
        return revenueService.getAllRevenues();
    }

//    @GetMapping("/revenue")
//    public List<Revenue> getRevenueByApartmentId(@RequestParam(required = false) String apartmentId) {
//        return revenueService.findAllRevenueByApartmentId(apartmentId);
//    }

    @PostMapping("/revenue")
    public Revenue createRevenue(@RequestBody RevenueDTO revenueDTO) {
        return revenueService.createRevenue(revenueDTO);
    }

    @GetMapping("/revenue")
    ResponseEntity<?> getRevenue(@RequestParam(required = false) Long id) {
        return revenueService.getRevenue(id);
    }

    // xoa theo ID
    @Transactional
    @DeleteMapping("/deleterevenue")
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

    // chinh sua khi ma thanh toan xong thay status => false
    @PutMapping("/revenue/{id}")
    public Revenue updateRevenue(@RequestBody RevenueDTO revenueDTO, @PathVariable Long id) {
        return revenueService.updateRevenueByID(revenueDTO, id);
    }

    @GetMapping("revenue/{apartmentId}")
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
