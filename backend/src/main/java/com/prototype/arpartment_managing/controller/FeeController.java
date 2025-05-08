package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5000")
@RequestMapping("/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    // View fee - Admin only
    @GetMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Fee> getFee(@PathVariable String type) {
        return feeService.getFeeByType(type);
    }

    // Create fee - Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFee(@RequestBody Fee fee) {
        feeService.createFee(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body(fee);
    }

    // Update fee - Admin only
    @PutMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Fee updateFee(@PathVariable String type, @RequestBody Fee fee) {
        return feeService.updateFee(fee, type);
    }

    // Delete fee - Admin only
    @DeleteMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFee(@PathVariable String type){
        feeService.deleteFeeByType(type);
        return ResponseEntity.status(HttpStatus.CREATED).body("Fee deleted successfully");
    }
}
