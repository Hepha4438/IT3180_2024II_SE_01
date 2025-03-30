package com.prototype.arpartment_managing.controller;


import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("https://it3180se01sprint1-9ecjvnlvb-hephas-projects.vercel.app/")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/fee/{type}")
    public Optional<Fee> getFee(@PathVariable String type) {
        return feeService.getFeeByType(type);
    }

    @PostMapping("/fee")
    public ResponseEntity<?> createFee(@RequestBody Fee fee) {
        feeService.createFee(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Fee created successfully");
    }

//    @PutMapping("/fee/{id}")
//    public Fee updateFee(@PathVariable long id, @RequestBody Fee f) {
//        return feeService.updateFee(f, id);
//    }

    @PutMapping("/fee/{type}")
    public Fee updateFee(@PathVariable String type, @RequestBody Fee fee) {
        return feeService.updateFee(fee, type);
    }
    @DeleteMapping("/fee/{type}")
    public ResponseEntity<?> deleteFee(@PathVariable String type){
        feeService.deleteFeeByType(type);
        return ResponseEntity.status(HttpStatus.CREATED).body("Fee deleted successfully");
    }

}
