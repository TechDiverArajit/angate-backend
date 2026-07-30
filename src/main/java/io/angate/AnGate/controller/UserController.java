package io.angate.AnGate.controller;

import io.angate.AnGate.dto.Auth.AuthRequest;
import io.angate.AnGate.dto.Auth.AuthResponse;
import io.angate.AnGate.dto.user.UserRequest;
import io.angate.AnGate.dto.user.UserResponse;
import io.angate.AnGate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) throws Exception{
        return new ResponseEntity<>(userService.register(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        return ResponseEntity.ok(userService.login(request));
    }
}
