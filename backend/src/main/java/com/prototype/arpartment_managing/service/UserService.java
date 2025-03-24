package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.token.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.prototype.arpartment_managing.repository.UserRepository;

import java.util.*;

@Primary
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password bằng BCrypt
        return userRepository.save(user);
    }

    public void newUser(UserDTO userDTO){
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
    }

    public ResponseEntity<?> getUser(String username, Long id){
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

    public void deleteUser(Long id){
        User user = userRepository.findById(id).
                orElseThrow(()-> new UserNotFoundException(id));
        Apartment apartment = user.getApartment();
        if(apartment != null && apartment.getResidents() != null){
            apartment.getResidents().remove(user);
            user.setApartment(null);
            apartment.setOccupants(apartment.getResidents().size());
            apartment.setIsOccupied(!apartment.getResidents().isEmpty());
            apartmentRepository.save(apartment);
        }
        userRepository.deleteById(id);
        return;
    }

    public ResponseEntity<?> loginUser(Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password,user.getPassword())) {
                JwtUtil jwtUtil = new JwtUtil();
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
    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Tên đăng nhập đã tồn tại"));
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Email đã được sử dụng"));
        }
        if (userRepository.findByCitizenIdentification(user.getCitizenIdentification()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Số CCCD đã được đăng kí"));
        }
        User newUser = createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("username", newUser.getUsername()));
    }


    public User updateUser(UserDTO userDTO, Long id){
        // Cập nhật thông tin căn hộ nếu apartmentId thay đổi
        return userRepository.findById(id)
                .map(user -> {
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

                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException(id));
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

}