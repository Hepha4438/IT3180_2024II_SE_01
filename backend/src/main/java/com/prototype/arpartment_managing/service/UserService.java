package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.token.JwtUtil;
import jakarta.transaction.Transactional;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.prototype.arpartment_managing.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    // Get all users
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    // Get user by id
    public ResponseEntity<?> getUser(Long id){
        if (id != null) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            return ResponseEntity.ok(new UserDTO(user));
        } else {
            return ResponseEntity.badRequest().body("Must provide id");
        }
    }

    // Get user by username
    public ResponseEntity<?> getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundExceptionUsername(username));
        return ResponseEntity.ok(new UserDTO(user));
    }

    public List<UserDTO> getUserSameApartment(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException(id));
        UserDTO userDTO = new UserDTO(user);
        Optional<Apartment> apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId());

        if (apartment.isPresent()) {
            List<User> users = userRepository.findByApartment(apartment.get());
            return users.stream().map(UserDTO::new).collect(Collectors.toList());
        }

        return Collections.emptyList(); // Trả về danh sách rỗng nếu không tìm thấy apartment
    }

    @Transactional
    // Create new user
    public User createUser(UserDTO userDTO){
        // Validate required fields
        if (userDTO.getApartmentId() == null || userDTO.getApartmentId().isEmpty()) {
            throw new IllegalArgumentException("Apartment ID is required");
        }
        
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCitizenIdentification(userDTO.getCitizenIdentification());

        // Set apartment (required)
        Apartment apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId())
                .orElseThrow(() -> new ApartmentNotFoundException(userDTO.getApartmentId()));
        user.setApartment(apartment);
        
        userRepository.save(user);
        
        // Update apartment
        apartment.getResidents().add(user);
        apartment.setOccupants(apartment.getResidents().size());
        apartment.setIsOccupied(!apartment.getResidents().isEmpty());
        apartmentRepository.save(apartment);
        
        return user;
    }

    // Delete user
    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id).
                orElseThrow(()-> new UserNotFoundException(id));

        Apartment apartment = user.getApartment();
        if(apartment != null && apartment.getResidents() != null){
            // Remove from list and break relationship
            apartment.getResidents().removeIf(r -> r.getId().equals(id));
            user.setApartment(null);

            // Update and save
            apartment.setOccupants(apartment.getResidents().size());
            apartment.setIsOccupied(!apartment.getResidents().isEmpty());
            apartmentRepository.save(apartment);
        }
        userRepository.deleteById(id);
    }

    // Login
    public ResponseEntity<?> loginUser(Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(username, user.getRole());
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("token", token);
                response.put("role", user.getRole());
                if (user.getApartment() != null) {
                    response.put("apartmentId", user.getApartment().getApartmentId());
                }

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    //Register
    public ResponseEntity<?> registerUser(UserDTO userDTO) {
        // Validate required fields
        if (userDTO.getApartmentId() == null || userDTO.getApartmentId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Apartment ID is required"));
        }
        
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Tên đăng nhập đã tồn tại"));
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Email đã được sử dụng"));
        }
        if (userRepository.findByCitizenIdentification(userDTO.getCitizenIdentification()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "Số CCCD đã được đăng kí"));
        }
        User newUser = createUser(userDTO);
        return ResponseEntity.ok(newUser);
    }

    // Transfer userDTO to User
    public User userDTOtouser(UserDTO userDTO,User user){
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCitizenIdentification(userDTO.getCitizenIdentification());

        if (userDTO.getApartmentId() != null) {
            Apartment apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId())
                    .orElseThrow(() -> new ApartmentNotFoundException(userDTO.getApartmentId()));
            user.setApartment(apartment);
        }
        return user;
    }

    // Update user information
    @Transactional
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
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    // Cập nhật thông tin căn hộ nếu apartmentId thay đổi
                    if (userDTO.getApartmentId() != null) {
                        Apartment apartment = apartmentRepository.findByApartmentId(userDTO.getApartmentId())
                                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found"));
                        user.setApartment(apartment);
                    }

                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }


    @Transactional
    public void removeUserFromPreviousApartment(User user) {
        Apartment previousApartment = user.getApartment();
        if (previousApartment != null && previousApartment.getResidents() != null) {
            previousApartment.getResidents().remove(user);
            previousApartment.setOccupants(previousApartment.getResidents().size());
            previousApartment.setIsOccupied(!previousApartment.getResidents().isEmpty());
            apartmentRepository.save(previousApartment);
        }
    }

    public Apartment getApartmentofUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return userOptional.get().getApartment();
    }



}