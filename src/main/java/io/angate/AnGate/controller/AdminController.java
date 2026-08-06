package io.angate.AnGate.controller;

import io.angate.AnGate.dto.user.UserResponse;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/assign-admin")
    public ResponseEntity<UserResponse> assignAdmin(@PathVariable Long userId){
        return ResponseEntity.ok(userService.assignAdmin(userId));
    }
}
