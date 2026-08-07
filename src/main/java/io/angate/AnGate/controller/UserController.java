package io.angate.AnGate.controller;

import io.angate.AnGate.dto.Auth.AuthRequest;
import io.angate.AnGate.dto.Auth.AuthResponse;
import io.angate.AnGate.dto.user.UserRequest;
import io.angate.AnGate.dto.user.UserResponse;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;

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
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest userRequest) throws Exception{
        return new ResponseEntity<>(userService.register(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        AuthResponse authResponse = userService.login(request);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)          // localhost
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()); // 7 days expiration
        return ResponseEntity.ok(new AuthResponse(authResponse.getUserId(), authResponse.getAccessToken(), null , authResponse.getExpiresDate()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            throw new ResourceNotFoundException("No cookies found");
        }
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token cookie not found"));
        AuthResponse authResponse = userService.refresh(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .sameSite("None")
                .path("/")
                .secure(true)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successfully");
    }

}
