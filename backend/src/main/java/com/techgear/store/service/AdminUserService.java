package com.techgear.store.service;

import com.techgear.store.dto.UserRequest;
import com.techgear.store.dto.UserResponse;
import com.techgear.store.entity.User;
import com.techgear.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AdminUserService(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<UserResponse> getAll(String keyword, String role, String status) {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            boolean match = true;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                boolean matchedKeyword =
                        (user.getFullName() != null && user.getFullName().toLowerCase().contains(kw))
                                || (user.getEmail() != null && user.getEmail().toLowerCase().contains(kw))
                                || (user.getPhone() != null && user.getPhone().toLowerCase().contains(kw));

                if (!matchedKeyword) {
                    match = false;
                }
            }

            if (role != null && !role.trim().isEmpty()) {
                if (user.getRole() == null || !user.getRole().equalsIgnoreCase(role.trim())) {
                    match = false;
                }
            }

            if (status != null && !status.trim().isEmpty()) {
                if (user.getStatus() == null || !user.getStatus().equalsIgnoreCase(status.trim())) {
                    match = false;
                }
            }

            if (match) {
                responses.add(mapResponse(user));
            }
        }

        return responses;
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapResponse(user);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        validateCreateRequest(request);

        String email = request.getEmail().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String tempPassword = generateTempPassword();

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(normalizeRole(request.getRole()));
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setAvatarUrl(request.getAvatarUrl());

        User savedUser = userRepository.save(user);

        emailService.sendAccountInfo(
                savedUser.getEmail(),
                savedUser.getFullName(),
                tempPassword
        );

        return mapResponse(savedUser);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateUpdateRequest(request);

        String email = request.getEmail().trim();

        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setRole(normalizeRole(request.getRole()));
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setAvatarUrl(request.getAvatarUrl());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        return mapResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(normalizeStatus(status));
        return mapResponse(userRepository.save(user));
    }

    @Transactional
    public String delete(Long id, String currentEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        userRepository.delete(user);
        return "Delete user successfully";
    }

    private void validateCreateRequest(UserRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    private void validateUpdateRequest(UserRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    private String normalizeRole(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();

        if (!r.equals("CUSTOMER") && !r.equals("STAFF") && !r.equals("OWNER")) {
            throw new IllegalArgumentException("Invalid role");
        }

        return r;
    }

    private String normalizeStatus(String status) {
        String s = (status == null || status.trim().isEmpty())
                ? "ACTIVE"
                : status.trim().toUpperCase();

        if (!s.equals("ACTIVE") && !s.equals("DISABLED")) {
            throw new IllegalArgumentException("Invalid status");
        }

        return s;
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private UserResponse mapResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }
}