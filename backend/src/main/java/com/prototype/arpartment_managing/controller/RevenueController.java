package com.prototype.arpartment_managing.controller;


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
    public List<Revenue> getRevenueByApartmentId(@RequestParam(required = false) String apartment_id) {
        return revenueService.findAllRevenueByApartmentId(apartment_id);
    }

    @PostMapping("/revenue")
    public Revenue addRevenue(@RequestBody Revenue revenue) {
        return revenueService.createRevenue(revenue);
    }

    @PutMapping("/revenue/{id}")
    public Revenue updateRevenue(@RequestBody Revenue revenue, @PathVariable Long id) {
        return revenueService.updateRevenueByID(id, revenue);
    }

    @DeleteMapping("/deleterevenue")
    public String deleteRevenue(@RequestBody Revenue revenue) {
        revenueService.deleteRevenueByID(revenue.getId());
        return "Deleted Revenue";
    }

    @DeleteMapping("/deleterevenue")
    public String deleteRevenueByID(@RequestParam Long id) {
        revenueService.deleteRevenueByID(id);
        return "Deleted Revenue";
    }

    @GetMapping("/revenue/{apartment_id}/{service}")
    public double getRevenueFee(@PathVariable String apartment_id, @PathVariable String service) {
        return revenueService.calculateFee(apartment_id, service);
    }

    @GetMapping("/revenue/total/{apartmentId}")
    public double getTotalRevenue(@PathVariable String apartmentId) {
        return revenueService.calculateTotalPayment(apartmentId);
    }
}
