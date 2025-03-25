package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.RevenueNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.FeeRepository;
import com.prototype.arpartment_managing.repository.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;


@Primary
@Service
public class RevenueService {
    @Autowired
    private RevenueRepository revenueRepository;

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    public List<Revenue> findAllRevenue() {
        return revenueRepository.findAll();
    }

    public List<Revenue> findAllRevenueByApartmentId(String apartmentId) {
        Optional<Apartment> apartment =  apartmentRepository.findByApartmentId(apartmentId);
        return apartment.map(Apartment::getRevenues).orElse(null);
    }

    public Revenue createRevenue(Revenue revenue) {
        return revenueRepository.save(revenue);
    }

    public Revenue findRevenueById(@PathVariable Long revenueId) {
        Optional<Revenue> revenue = revenueRepository.findById(revenueId);
        return revenue.orElse(null);
    }

    public boolean deleteRevenueByID(Long revenueId){
        Optional<Revenue> revenue = revenueRepository.findById(revenueId);
        if(revenue.isPresent()){
            revenueRepository.delete(revenue.get());
            return true;
        }
        return false;
    }

    public Revenue updateRevenueByID(Long id, Revenue revenue) {
        return revenueRepository.findById(id)
                .map(existingRevenue -> {
                    // Cập nhật dữ liệu từ request vào bản ghi cũ
                    existingRevenue.setStatus(revenue.getStatus());
                    existingRevenue.setType(revenue.getType());
                    existingRevenue.setUsed(revenue.getUsed());

                    return revenueRepository.save(existingRevenue);
                })
                .orElseThrow(() -> new RevenueNotFoundException(id));
    }



    public Double calculateFee(String apartmentId, String feeType) {
        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);

        return revenues.stream()
                .filter(revenue -> feeType.equals(revenue.getType()))
                .map(revenue -> {
                    Optional<Fee> feeOpt = feeRepository.findByTypeAndApartment_ApartmentId(feeType, revenue.getApartment().getApartmentId());
                    return feeOpt.map(fee -> revenue.getApartment().getArea() * fee.getPricePerUnit()).orElse(0.0);
                })
                .reduce(0.0, Double::sum);
    }


    public Double calculateTotalPayment(String apartmentId) {
        // Lấy danh sách các mục tiêu thụ của căn hộ
        List<Revenue> revenues = revenueRepository.findByApartmentId(apartmentId);
        if (revenues.isEmpty()) {
            throw new RuntimeException("No revenue records found for apartment: " + apartmentId);
        }

        double totalPayment = 0.0;

        // Duyệt từng loại tiêu thụ để tính tiền
        for (Revenue revenue : revenues) {
            // Tìm mức giá tương ứng với loại tiêu thụ

            Fee fee = feeRepository.findByTypeAndApartment_ApartmentId(revenue.getType() ,apartmentId)
                    .orElseThrow(() -> new RuntimeException("Fee not found for type: " + revenue.getType()));

            // Tính tiền của mục tiêu thụ này
            double cost = revenue.getUsed() * fee.getPricePerUnit();
            totalPayment += cost;
        }

        return totalPayment;
    }




}

