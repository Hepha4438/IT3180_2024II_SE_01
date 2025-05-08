package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.controller.FeeController;
import com.prototype.arpartment_managing.exception.FeeNotFoundException;
import com.prototype.arpartment_managing.exception.RevenueNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.repository.FeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;


    public Fee createFee(Fee fee) {
        return feeRepository.save(fee);
    }

    public Optional<Fee> getFee(String type) {
        return feeRepository.findByType(type);
    }

//    public Fee getFeeByType(String type) {
//        Optional<Fee> fee =  feeRepository.findByType(type);
//        return fee.orElse(null);
//    }

    public Optional<Fee> getFeeByType(String type){
        return feeRepository.findByType(type);
    }
//    public Fee updateFee(Fee fee, Long id) {
//        return feeRepository.findById(id)
//                .map(existingRevenue -> {
//                    existingRevenue.setPricePerUnit(fee.getPricePerUnit());
//                    existingRevenue.setType(fee.getType());
//
//                    return feeRepository.save(existingRevenue);
//                })
//                .orElseThrow(() -> new RevenueNotFoundException(id));
//
//    }

    public Fee updateFee(Fee fee, String type) {
        return feeRepository.findByType(type)
                .map(existingFee -> {
                    existingFee.setPricePerUnit(fee.getPricePerUnit());
                    existingFee.setType(fee.getType());
                    return feeRepository.save(existingFee);
                })
                .orElseThrow(() -> new FeeNotFoundException(type));
    }

    @Transactional
    public void deleteFeeByType(String type) {
        Fee fee = feeRepository.findByType(type)
                .orElseThrow(() -> new FeeNotFoundException(type));
        feeRepository.delete(fee);
    }

    public Iterable<Fee> getAllFees() {
        return feeRepository.findAll();
    }

}
