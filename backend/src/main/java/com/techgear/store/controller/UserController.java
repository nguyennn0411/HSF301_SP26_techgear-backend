package com.techgear.store.controller;

import com.techgear.store.dto.ChangePasswordDTO;
import com.techgear.store.dto.UserProfileDTO;
import com.techgear.store.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    // Ai đăng nhập cũng xem được profile của chính mình
    @GetMapping("/me")
    public UserProfileDTO me(Authentication auth) {
        return userService.getProfileByEmail(auth.getName());
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication auth, @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(auth.getName(), dto);
        return "Change password success";
    }

    // Ví dụ endpoint chỉ OWNER mới vào được
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    @GetMapping("/admin-only")
    public String ownerOnly() {
        return "Hello OWNER";
    }
}
