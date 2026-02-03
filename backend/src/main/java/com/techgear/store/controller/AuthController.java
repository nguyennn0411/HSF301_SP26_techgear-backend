package com.techgear.store.controller;

import com.techgear.store.dto.JwtResponse;
import com.techgear.store.dto.LoginRequest;
import com.techgear.store.dto.SignupRequest;
import com.techgear.store.entity.Cart;
import com.techgear.store.entity.User;
import com.techgear.store.repository.CartRepository;
import com.techgear.store.repository.UserRepository;
import com.techgear.store.security.JwtUtils;
import com.techgear.store.service.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          CartRepository cartRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public String register(@RequestBody SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User u = new User();
        u.setFullName(req.getFullName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole("ROLE_CUSTOMER");
        u.setStatus("ACTIVE");

        u = userRepository.save(u);

        // auto create cart
        Cart c = new Cart();
        c.setUser(u);
        cartRepository.save(c);

        return "Register success";
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        // Lấy role từ authorities (hoặc query user)
        String role = principal.getAuthorities().iterator().next().getAuthority();

        String token = jwtUtils.generateToken(principal.getUsername(), role, principal.getId());

        return new JwtResponse(token, principal.getId(), principal.getUsername(), role);
    }

    // JWT logout thường là FE xóa token; nếu bạn muốn blacklist token thì làm thêm table/redis
    @PostMapping("/logout")
    public String logout() {
        return "Logged out (client should delete token)";
    }
}
