package com.prototype.arpartment_managing.controller;


import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:5000")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/feeId")
    public Fee getFee(long id) {
        return feeService.getFee(id);
    }

    @GetMapping("/fee")
    public Fee getFee(String type) {
        return feeService.getFeeByType(type);
    }

    @PostMapping("/createFee")
    public Fee createFee(@RequestBody Fee f) {
        return feeService.createFee(f);
    }

    @PutMapping("/fee/{id}")
    public Fee updateFee(@PathVariable long id, @RequestBody Fee f) {
        return feeService.updateFee(f, id);
    }

    @PutMapping("/fee")
    public Fee updateFee(@RequestParam String type, @RequestBody Fee f) {
        return feeService.updateFee(f, type);
    }
}
