package io.angate.AnGate.service;

import io.angate.AnGate.dto.Auth.AuthRequest;
import io.angate.AnGate.dto.Auth.AuthResponse;
import io.angate.AnGate.dto.user.UserRequest;
import io.angate.AnGate.dto.user.UserResponse;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.entity.enums.UserStatus;
import io.angate.AnGate.exception.BookingExistsDeletionException;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    public UserResponse getUserById(Long userId){
        Users user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("No user exists"));
        return modelMapper.map(user,UserResponse.class);
    }

    public UserResponse register(UserRequest userRequest)  {
        if (userRepository.findByEmailId(userRequest.getEmailId()).isPresent()) {
            throw new BookingExistsDeletionException("Email already in use");
        }
        Users userTobeSaved = modelMapper.map(userRequest, Users.class);
        userTobeSaved.setRole(Users.Role.USER);
        userTobeSaved.setStatus(UserStatus.ACTIVE);
        userTobeSaved.setEmailId(userRequest.getEmailId().trim().toLowerCase());
        userTobeSaved.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Users user = userRepository.save(userTobeSaved);
        return modelMapper.map(user, UserResponse.class);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword()));
        Users users = (Users) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(users);
        String refreshToken = jwtService.generateRefreshToken(users);
        Claims claims = jwtService.extractClaim(accessToken);
        return new AuthResponse(users.getId(),accessToken,refreshToken,claims.getExpiration());
    }



    public AuthResponse refresh(String refreshToken){
        Claims claims1 = jwtService.extractClaim(refreshToken);
        if(!"refresh".equals(claims1.get("type"))){
            throw new RuntimeException("Invalid refresh token");
        }
        Long id = jwtService.getUserIdFromToken(refreshToken);
        Users users = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("no user exists"));
        String accessToken = jwtService.generateAccessToken(users);
        return new AuthResponse(users.getId(),accessToken,refreshToken,claims1.getExpiration());
    }


    @Transactional
    public UserResponse assignAdmin(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No user found"));
        if(user.getRole()== Users.Role.ADMIN){
            throw new BookingExistsDeletionException("User is already an admin");
        }
        user.setRole(Users.Role.ADMIN);
        user = userRepository.save(user);
        return modelMapper.map(user,UserResponse.class);
    }
}

