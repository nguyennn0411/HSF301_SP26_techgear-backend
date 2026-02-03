package com.techgear.store.service;

import com.techgear.store.dto.ChangePasswordDTO;
import com.techgear.store.dto.UserProfileDTO;
import com.techgear.store.entity.User;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDTO getProfileByEmail(String email) {
        User u = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return toProfileDTO(u);
    }

    public void changePassword(String email, ChangePasswordDTO dto) {
        User u = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), u.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }

        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(u);
    }

    private UserProfileDTO toProfileDTO(User u) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setRole(u.getRole());
        dto.setStatus(u.getStatus());
        return dto;
    }
}
