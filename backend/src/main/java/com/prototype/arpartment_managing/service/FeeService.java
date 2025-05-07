package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.controller.FeeController;
import com.prototype.arpartment_managing.exception.FeeNotFoundException;
import com.prototype.arpartment_managing.exception.RevenueNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    @Transactional
    public Fee createFee(Fee fee) {
        if (fee.getPricePerUnit() <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than 0");
        }
        return feeRepository.save(fee);
    }

    public Optional<Fee> getFeeByType(String type){
        return feeRepository.findByType(type);
    }

    @Transactional
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
                .orElseThrow(()-> new FeeNotFoundException(type));
        feeRepository.deleteByType(type);
        return;
    }

}
