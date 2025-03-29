package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.service.UserService;
import com.prototype.arpartment_managing.token.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.prototype.arpartment_managing.service.ApartmentResidentService;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:5000")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentResidentService apartmentResidentService;

    // Get all users
    @GetMapping("/all")
    List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    // Tạo user mới
    @PostMapping("/create")
    public ResponseEntity<?> newUser(@RequestBody UserDTO userDTO) {
        userService.newUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    // Tìm kiếm user
    @GetMapping("/profile")
    ResponseEntity<?> getUser(@RequestParam(required = false) String username, @RequestParam(required = false) Long id) {
        return userService.getUser(username, id);
    }
    // Xóa User
    @Transactional
    @DeleteMapping("/delete")
    ResponseEntity<?> deleteUser(@RequestParam(required = false) Long id){
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("User delete successfully");
    }
    // Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        return userService.loginUser(loginRequest);
    }
    //Register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // Update your existing updateUser method to include apartment synchronization
    @PutMapping("/{id}")
    User updateUser(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        return userService.updateUser(userDTO, id);
    }
}
