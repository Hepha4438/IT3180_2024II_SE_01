package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.service.EmailService;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.service.ApartmentResidentService;
import com.prototype.arpartment_managing.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentResidentService apartmentResidentService;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private Map<String, String> otpStorage = new HashMap<>();

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        return userService.loginUser(loginRequest);
    }
    //Register
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }

    // Get all users
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    // Tạo user mới
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> newUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    // Tìm kiếm user bằng username
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @GetMapping("/profile/{username}")
    ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
    // Xóa User
    @Transactional
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> deleteUser(@RequestParam(required = false) Long id){
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("User delete successfully");
    }

    // Update your existing updateUser method to include apartment synchronization
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    User updateUser(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        return userService.updateUser(userDTO, id);
    }

    @GetMapping("/{id}/apartmentresident")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public List<UserDTO> getUserinApartment(@PathVariable Long id) {
        return userService.getUserSameApartment(id);
    }

    @GetMapping("/{id}/apartment")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public Apartment getApartmentOfUser(@PathVariable Long id){
        return userService.getApartmentofUser(id);
    }

    // Initial admin setup - no authentication required
    @PostMapping("/setup")
    public ResponseEntity<?> setupInitialAdmin(@RequestBody UserDTO userDTO) {
        // Check if any user exists
        if (userRepository.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Initial setup already completed. Please use regular registration.");
        }

        // Validate required fields
        if (userDTO.getApartmentId() == null || userDTO.getApartmentId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Apartment ID is required"));
        }

        // Force role to ADMIN for first user
        userDTO.setRole("ADMIN");
        return userService.registerUser(userDTO);
    }

    @PostMapping("/forget-password")
    public Map<String, Object> sendOtp(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String username = requestBody.get("username");
        String email = requestBody.get("email");

        // Kiểm tra sự khớp giữa username và email
        if (userService.isUsernameMatchingEmail(username, email)) {
            System.out.println("Username and email match!");
            String otp = String.format("%06d", new Random().nextInt(999999));
            otpStorage.put(email, otp);

            try {
                emailService.sendOTPViaEmail(email, otp);
                response.put("valid", true);
            } catch (MessagingException e) {
                response.put("valid", false);
                response.put("error", "Failed to send email.");
                e.printStackTrace();
            }
        } else {
            response.put("valid", false);
            response.put("error", "Username and email do not match.");
        }

        return response;
    }

    @GetMapping("/verify-otp")
    public Map<String, Object> verifyOtp(@RequestParam String otp) {
        Map<String, Object> response = new HashMap<>();

        boolean matched = otpStorage.values().stream().anyMatch(code -> code.equals(otp));
        response.put("valid", matched);

        return response;
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("password");

        boolean changed = userService.changePassword(username, newPassword);
        if (changed) {
            System.out.println("Password changed successfully.");
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
        }
    }

}
