package io.angate.AnGate.service;

import io.angate.AnGate.dto.user.UserRequest;
import io.angate.AnGate.dto.user.UserResponse;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public UserResponse getUserById(Long userId){
        Users user = userRepository.findById(userId).orElseThrow();
        return modelMapper.map(user,UserResponse.class);
    }

    public UserResponse register(UserRequest userRequest) throws CredentialException {
        if (userRepository.findByEmailId(userRequest.getEmailId()).isPresent()) {
            throw new CredentialException("Email already in use");
        }

        Users userTobeSaved = modelMapper.map(userRequest, Users.class);

//        Users userTobeSaved = Users.builder()
//                .fullName(userRequest.getFullName())
//                .emailId(userRequest.getEmailId())
//                .password(userRequest.getPassword())
//                .gender(userRequest.getGender())
//                .isActive(Boolean.TRUE)
//                .build();
        Users user = userRepository.save(userTobeSaved);
        return modelMapper.map(user, UserResponse.class);
    }

}

