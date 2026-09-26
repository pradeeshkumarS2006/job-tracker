package com.pradeesh.jobtracker.controller;

import com.pradeesh.jobtracker.dto.AuthDtos.AuthRequest;
import com.pradeesh.jobtracker.dto.AuthDtos.AuthResponse;
import com.pradeesh.jobtracker.entity.User;
import com.pradeesh.jobtracker.repository.UserRepository;
import com.pradeesh.jobtracker.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User user = new User(request.username, passwordEncoder.encode(request.password));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwtUtil.generateToken(user.getUsername())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.username).orElse(null);

        if (user == null || !passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(user.getUsername())));
    }
}
