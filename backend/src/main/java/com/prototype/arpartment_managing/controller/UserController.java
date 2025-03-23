package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.prototype.arpartment_managing.service.ApartmentResidentService;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:5000")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ApartmentResidentService apartmentResidentService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//    @PostMapping("/user")
//    User newUser(@RequestBody User newUser){
//        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
//        return userRepository.save(newUser);
//    }

    // Get all users
    @GetMapping("/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // Tạo user mới
    @PostMapping("/user")
    public ResponseEntity<?> newUser(@RequestBody UserDTO userDTO) {
        // Tạo user từ DTO
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCitizenIdentification(userDTO.getCitizenIdentification());

        // Nếu có apartmentId, tìm căn hộ tương ứng và gán cho user
        if (userDTO.getApartmentId() != null) {
            Apartment apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId())
                    .orElseThrow(() -> new ApartmentNotFoundException(userDTO.getApartmentId()));
            user.setApartment(apartment);
        }
        userRepository.save(user);
        if (user.getApartment() != null) {
            Apartment apartment = user.getApartment();
            apartment.getResidents().add(user);
            apartment.setOccupants(apartment.getResidents().size());
            apartment.setIsOccupied(!apartment.getResidents().isEmpty());
            apartmentRepository.save(apartment);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    // Tìm kiếm user
    @GetMapping("/user")
    ResponseEntity<?> getUser(@RequestParam(required = false) String username, @RequestParam(required = false) Long id) {
        User user;
        if (username != null) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundExceptionUsername(username));
        } else if (id != null) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
        } else {
            return ResponseEntity.badRequest().body("Must provide either username or id");
        }

        return ResponseEntity.ok(new UserDTO(user));
    }


    // Xóa User
    @Transactional
    @DeleteMapping("/deleteuser")
    String deleteUser(@RequestParam(required = false) Long id){
        User user = userRepository.findById(id)
                        .orElseThrow(()-> new UserNotFoundException(id));
        Apartment apartment = user.getApartment();
        if(apartment != null){
            apartment.getResidents().remove(user);
            user.setApartment(null);
            apartment.setOccupants(apartment.getResidents().size());
            apartment.setIsOccupied(!apartment.getResidents().isEmpty());

            apartmentRepository.save(apartment);
        }
        userRepository.deleteById(id);
        return "User with id "+id+" has been deleted sucessfully";
    }
    // Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password,user.getPassword())) {
                String token = jwtUtil.generateToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("token",token);
                response.put("role", user.getRole());

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }
    //Register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Tên đăng nhập đã tồn tại"));
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Email đã được sử dụng"));
        }
        if (userRepository.findByCitizenIdentification(user.getCitizenIdentification()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Số CCCD đã được đăng kí"));
        }
        User newUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("username", newUser.getUsername()));
    }

    public void removeUserFromPreviousApartment(User user) {
        Apartment previousApartment = user.getApartment();
        if (previousApartment != null && previousApartment.getResidents() != null) {
            previousApartment.getResidents().remove(user);
            previousApartment.setOccupants(previousApartment.getResidents().size());
            previousApartment.setIsOccupied(!previousApartment.getResidents().isEmpty());
            apartmentRepository.save(previousApartment);
        }
    }

    // Update your existing updateUser method to include apartment synchronization
    @PutMapping("/user/{id}")
    User updateUser(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        User updatedUser = userRepository.findById(id)
               .map(user -> {
//                    user.setUsername(newUser.getUsername());
//                    user.setFullName(newUser.getFullName());
//                    user.setEmail(newUser.getEmail());
//                    user.setPhoneNumber(newUser.getPhoneNumber());
//                    user.setCitizenIdentification(newUser.getCitizenIdentification());
//                    user.setRole(newUser.getRole());
//                    user.setPassword(newUser.getPassword());
//                    user.setApartment(newUser.getApartment());

                    user.setFullName(userDTO.getFullName());
                    user.setUsername(userDTO.getUsername());
                    user.setEmail(userDTO.getEmail());
                    user.setPhoneNumber(userDTO.getPhoneNumber());
                    user.setRole(userDTO.getRole());
                    user.setCitizenIdentification(userDTO.getCitizenIdentification());
                    user.setPassword(userDTO.getPassword());
                   // Cập nhật thông tin căn hộ nếu apartmentId thay đổi
                   if (userDTO.getApartmentId() != null) {
                       Apartment apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId())
                               .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found"));
                       user.setApartment(apartment);
                   }

//                    // Check if apartment assignment has changed
//                    boolean apartmentChanged = (user.getApartment() == null && newUser.getApartment() != null) ||
//                            (user.getApartment() != null && newUser.getApartment() == null) ||
//                            (user.getApartment() != null && newUser.getApartment() != null &&
//                                    !user.getApartment().getApartmentId().equals(newUser.getApartment().getApartmentId()));
//
//                    // Update apartment assignment
//                    user.setApartment(newUser.getApartment());
//
//                    // Save the user first
//                    User savedUser = userRepository.save(user);
//
//                    // If apartment changed, update apartment resident list
//                    if (apartmentChanged) {
//                        try {
//                            // Use the service to update apartment-resident relationship
//                            apartmentResidentService.updateUserApartmentAssignment(savedUser);
//                        } catch (Exception e) {
//                            // Log the error but don't fail the user update
//                            System.err.println("Failed to update apartment resident list: " + e.getMessage());
//                        }
//                    }

                   //return savedUser;
                   return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException(id));

        return updatedUser;
    }
}

