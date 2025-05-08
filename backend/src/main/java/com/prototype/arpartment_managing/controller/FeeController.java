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
@RequestMapping("/fees")
@CrossOrigin("http://localhost:3000")
public class FeeController {

    @Autowired
    private FeeService feeService;

    // Lấy tất cả các phí
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<Fee> getAllFees() {
        return feeService.getAllFees();
    }

    // Lấy phí theo type
    @GetMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFee(@PathVariable String type) {
        Optional<Fee> fee = feeService.getFeeByType(type);
        // Nếu Fee tồn tại, trả về 200 OK cùng với dữ liệu Fee
        if (fee.isPresent()) {
            return ResponseEntity.ok(fee.get());
        } else {
            // Nếu Fee không tồn tại, trả về 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fee not found");
        }
    }

    // Create fee - Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFee(@RequestBody Fee fee) {
        feeService.createFee(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Fee created successfully");
    }

    // Cập nhật phí theo type (chỉ cần sửa pricePerUnit, nhưng vẫn gửi toàn bộ Fee)
    @PutMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFee(@PathVariable String type, @RequestBody Fee fee) {
        Fee updated = feeService.updateFee(fee, type);
        return ResponseEntity.ok(updated);
    }

    // Xóa phí theo type
    @DeleteMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFee(@PathVariable String type) {
        feeService.deleteFeeByType(type);
        return ResponseEntity.ok("Fee deleted successfully");
    }

}
