package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.dto.RevenueDTO;
import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.RevenueNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.FeeRepository;
import com.prototype.arpartment_managing.repository.RevenueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
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
    public List<Revenue> getAllRevenues() {
        return revenueRepository.findAll();
    }


    // Get Revenue Information
    public ResponseEntity<?> getRevenue(Long id){
        Revenue revenue;
        if (id != null) {
            revenue = revenueRepository.findById(id)
                    .orElseThrow(() -> new RevenueNotFoundException(id));
        } else {
            return ResponseEntity.badRequest().body("Must provide id");
        }
        return ResponseEntity.ok(new RevenueDTO(revenue));
    }

    @Transactional
    // Create Revenue
    public Revenue createRevenue(RevenueDTO revenueDTO) {
        Revenue revenue = new Revenue();
        revenue.setUsed(revenueDTO.getUsed());
        revenue.setStatus(revenueDTO.getStatus());
        revenue.setType(revenueDTO.getType());

        // Validate Apartment ID
        if (revenueDTO.getApartmentId() == null) {
            throw new IllegalArgumentException("Apartment ID must not be null");
        }

        Apartment apartment = apartmentRepository.findByApartmentId(revenueDTO.getApartmentId())
                .orElseThrow(() -> new ApartmentNotFoundException(revenueDTO.getApartmentId()));

        revenue.setApartment(apartment);

        Optional<Fee> feeOpt = feeRepository.findByType(revenueDTO.getType());
        double calculatedTotal = feeOpt.map(fee -> revenueDTO.getUsed() * fee.getPricePerUnit()).orElse(0.0);

        revenue.setTotal(calculatedTotal);

        revenue = revenueRepository.save(revenue);

        apartment.getRevenues().add(revenue);
        apartment.setTotal(calculateTotalPayment(apartment.getApartmentId()));
        apartmentRepository.save(apartment);

        return revenue;
    }



    // Delete Revenue
    public void deleteRevenue(Long id) {
        Revenue revenue = revenueRepository.findById(id)
                .orElseThrow(() -> new RevenueNotFoundException(id));

        Apartment apartment = revenue.getApartment();
        if (apartment != null) {
            apartment.getRevenues().removeIf(r -> r.getId().equals(id));
            revenue.setApartment(null);
            apartmentRepository.save(apartment);
        }

        revenueRepository.deleteById(id);
    }



    // update khoan thu theo id
    public Revenue updateRevenueByID(RevenueDTO revenueDTO, Long id) {
        return revenueRepository.findById(id)
                .map(existingRevenue -> {
                    // Cập nhật dữ liệu từ request vào bản ghi cũ
                    existingRevenue.setStatus(revenueDTO.getStatus());
                    existingRevenue.setType(revenueDTO.getType());
                    existingRevenue.setUsed(revenueDTO.getUsed());

                    return revenueRepository.save(existingRevenue);
                })
                .orElseThrow(() -> new RevenueNotFoundException(id));
    }


//    // tinh khoan thu theo loai cua 1 can ho (theo dien tich)
//    public Double calculateFee(String apartmentId, String feeType) {
//        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);
//
//        return revenues.stream()
//                .filter(revenue -> feeType.equals(revenue.getType()))
//                .map(revenue -> {
//                    Optional<Fee> feeOpt = feeRepository.findByType(feeType);
//                    return feeOpt.map(fee -> revenue.getUsed() * fee.getPricePerUnit()).orElse(0.0);
//                })
//                .reduce(0.0, Double::sum);
//    }

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

    // danh sach full khoan thu cua 1 can ho
    public List<Revenue> findAllRevenueByApartmentId(String apartmentId) {
        Optional<Apartment> apartment =  apartmentRepository.findByApartmentId(apartmentId);
        return apartment.map(Apartment::getRevenues).orElse(null);
    }



}

