package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.dto.RevenueDTO;
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

    // danh sach full khoan thu cua tat ca can ho
    public List<Revenue> findAllRevenue() {
        return revenueRepository.findAll();
    }

    // danh sach full khoan thu cua 1 can ho
    public List<Revenue> findAllRevenueByApartmentId(String apartmentId) {
        Optional<Apartment> apartment =  apartmentRepository.findByApartmentId(apartmentId);
        return apartment.map(Apartment::getRevenues).orElse(null);
    }

    // tao 1 khoan thu
    public Revenue createRevenue(RevenueDTO revenueDTO) {
        Revenue revenue = new Revenue();
        revenue.setUsed(revenueDTO.getUsed());
        revenue.setStatus(revenueDTO.getStatus());
        revenue.setType(revenueDTO.getType());
        revenue.setId(revenueDTO.getId());
        if (revenueDTO.getApartmentId() != null) {
            Apartment apartment = apartmentRepository.findByApartmentId(revenueDTO.getApartmentId())
                    .orElseThrow(() -> new ApartmentNotFoundException(revenueDTO.getApartmentId()));
            revenue.setApartment(apartment);
        }
        revenueRepository.save(revenue);
        if (revenue.getApartment() != null) {
            Apartment apartment = revenue.getApartment();
            apartment.getRevenues().add(revenue);
            apartmentRepository.save(apartment);
        }
        return revenueRepository.save(revenue);
    }

    // tim khoan thu theo cot ID
    public Revenue findRevenueById(@PathVariable Long revenueId) {
        Optional<Revenue> revenue = revenueRepository.findById(revenueId);
        return revenue.orElse(null);
    }

    // xoa khoan thu theo id
    public boolean deleteRevenueByID(Long revenueId){
        Optional<Revenue> revenue = revenueRepository.findById(revenueId);
        if(revenue.isPresent()){
            revenueRepository.delete(revenue.get());
            return true;
        }
        return false;
    }


    // update khoan thu theo id
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


    // tinh khoan thu theo loai cua 1 can ho (theo dien tich)
    public Double calculateFee(String apartmentId, String feeType) {
        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);

        return revenues.stream()
                .filter(revenue -> feeType.equals(revenue.getType()))
                .map(revenue -> {
                    Optional<Fee> feeOpt = feeRepository.findByType(feeType);
                    return feeOpt.map(fee -> revenue.getUsed() * fee.getPricePerUnit()).orElse(0.0);
                })
                .reduce(0.0, Double::sum);
    }

    // tinh tong khoan thu cua 1 can ho
    public Double calculateTotalPayment(String apartmentId) {
        // Lấy danh sách các mục tiêu thụ của căn hộ
        List<Revenue> revenues = revenueRepository.findByApartment_ApartmentId(apartmentId);
        if (revenues.isEmpty()) {
            throw new RuntimeException("No revenue records found for apartment: " + apartmentId);
        }

        double totalPayment = 0.0;

        // Duyệt từng loại tiêu thụ để tính tiền
        for (Revenue revenue : revenues) {
            // Tìm mức giá tương ứng với loại tiêu thụ

            Fee fee = feeRepository.findByType(revenue.getType())
                    .orElseThrow(() -> new RuntimeException("Fee not found for type: " + revenue.getType()));

            // Tính tiền của mục tiêu thụ này
            double cost = revenue.getUsed() * fee.getPricePerUnit();
            totalPayment += cost;
        }
        return totalPayment;
    }



}

