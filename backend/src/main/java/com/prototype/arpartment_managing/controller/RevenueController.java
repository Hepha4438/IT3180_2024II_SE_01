package com.prototype.arpartment_managing.controller;


import com.prototype.arpartment_managing.dto.RevenueDTO;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5000")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/revenues")
    public List<Revenue> getRevenues() {
        return revenueService.findAllRevenue();
    }

    @GetMapping("/revenue")
    public List<Revenue> getRevenueByApartmentId(@RequestParam(required = false) String apartmentId) {
        return revenueService.findAllRevenueByApartmentId(apartmentId);
    }

    @PostMapping("/revenue")
    public Revenue addRevenue(@RequestBody RevenueDTO revenueDTO) {
        return revenueService.createRevenue(revenueDTO);
    }

    // chinh sua khi ma thanh toan xong thay status => false
    @PutMapping("/revenue/{id}")
    public Revenue updateRevenue(@RequestBody Revenue revenue, @PathVariable Long id) {
        return revenueService.updateRevenueByID(id, revenue);
    }

    // xong 1 khoan thu => xoa => can status ??
    @DeleteMapping("/deleterevenue")
    public String deleteRevenue(@RequestBody Revenue revenue) {
        revenueService.deleteRevenueByID(revenue.getId());
        return "Deleted Revenue";
    }
    // xoa theo ID
    @DeleteMapping("/deleterevenuebyID")
    public String deleteRevenueByID(@RequestParam Long id) {
        revenueService.deleteRevenueByID(id);
        return "Deleted Revenue";
    }

    // tinh tien 1 khoan thu
    @GetMapping("/revenue/{apartment_id}/{fee}")
    public double getRevenueFee(@PathVariable String apartment_id, @PathVariable String fee) {
        return revenueService.calculateFee(apartment_id, fee);
    }

    //tinh tong khoan thu
    @GetMapping("/revenue/total/{apartmentId}")
    public double getTotalRevenue(@PathVariable String apartmentId) {
        return revenueService.calculateTotalPayment(apartmentId);
    }
}
