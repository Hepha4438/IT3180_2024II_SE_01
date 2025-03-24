package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.FeeRepository;
import com.prototype.arpartment_managing.repository.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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

    public boolean deleteRevenue(Long revenueId){
        Optional<Revenue> revenue = revenueRepository.findById(revenueId);
        if(revenue.isPresent()){
            revenueRepository.delete(revenue.get());
            return true;
        }
        return false;
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
            Fee fee = feeRepository.findByTypeAndApartmentId(revenue.getType() ,apartmentId)
                    .orElseThrow(() -> new RuntimeException("Fee not found for type: " + revenue.getType()));

            // Tính tiền của mục tiêu thụ này
            double cost = revenue.getUsed() * fee.getPricePerUnit();
            totalPayment += cost;
        }

        return totalPayment;
    }
}

