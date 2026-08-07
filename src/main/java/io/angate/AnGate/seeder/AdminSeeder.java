package io.angate.AnGate.seeder;

import io.angate.AnGate.entity.Users;
import io.angate.AnGate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.pass}")
    private String adminPass;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.existsByEmailId(adminEmail)){
            return;
        }

        Users users = Users.builder()
                .emailId(adminEmail)
                .password(passwordEncoder.encode(adminPass))
                .fullName("AnGATE Corporation")
                .role(Users.Role.ADMIN)
                .build();
        userRepository.save(users);
        System.out.println("Admin Created");
    }
}
