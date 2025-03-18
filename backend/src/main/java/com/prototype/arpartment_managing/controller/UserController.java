package com.prototype.arpartment_managing.controller;

import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionEmail;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.prototype.arpartment_managing.service.UserService;
import com.prototype.arpartment_managing.token.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:5000")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        return userRepository.save(newUser);
    }
    // All User
    @GetMapping("/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }
    // ID
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException(id));
    }
    // Email
    @GetMapping("/user/email/{email}")
    User getUserByEmail(@PathVariable String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundExceptionEmail(email));
    }
    //Username
    @GetMapping("/user/username/{username}")
    User getUserByUsername(@PathVariable String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundExceptionUsername(username));
    }

    //Update User
    @PutMapping("/user/{id}")
    User updateUser(@RequestBody User newUser,@PathVariable Long id){
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setFullName(newUser.getFullName());
                    user.setEmail(newUser.getEmail());
                    user.setPhoneNumber(newUser.getPhoneNumber());
                    user.setCitizenIdentification(newUser.getCitizenIdentification());
                    user.setRole(newUser.getRole());
                    user.setPassword(newUser.getPassword());
                    user.setRoom(newUser.getRoom());
                    return userRepository.save(user);
                }).orElseThrow(()->new UserNotFoundException(id));
    }

    //Xóa User
    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException(id);
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
}
