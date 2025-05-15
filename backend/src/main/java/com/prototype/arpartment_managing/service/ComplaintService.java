package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.exception.ComplaintNotFoundException;
import com.prototype.arpartment_managing.model.Complaint;
import com.prototype.arpartment_managing.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Primary
@Service
public class ComplaintService {
    
    private final ComplaintRepository complaintRepository;

    @Autowired
    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public ResponseEntity<?> getComplaintById(Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Must provide complaint id");
        }
        
        return complaintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ComplaintNotFoundException(id));
    }

    public ResponseEntity<?> createComplaint(Complaint complaint) {
        if (complaint == null) {
            return ResponseEntity.badRequest().body("Complaint data is required");
        }
        
        complaint.setCreatedAt(LocalDateTime.now());
        Complaint createdComplaint = complaintRepository.save(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComplaint);
    }

    public ResponseEntity<?> updateComplaint(Long id, Complaint updatedComplaint) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Must provide complaint id");
        }
        
        return complaintRepository.findById(id)
                .map(existingComplaint -> {
                    existingComplaint.setTitle(updatedComplaint.getTitle());
                    existingComplaint.setDescription(updatedComplaint.getDescription());
                    existingComplaint.setCategory(updatedComplaint.getCategory());
                    return ResponseEntity.ok(complaintRepository.save(existingComplaint));
                })
                .orElseThrow(() -> new ComplaintNotFoundException(id));
    }

    public ResponseEntity<?> deleteComplaint(Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Must provide complaint id");
        }
        
        if (!complaintRepository.existsById(id)) {
            throw new ComplaintNotFoundException(id);
        }
        
        complaintRepository.deleteById(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
} 