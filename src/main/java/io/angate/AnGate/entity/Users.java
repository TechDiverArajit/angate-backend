package io.angate.AnGate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.angate.AnGate.entity.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users extends BaseEntity implements UserDetails {

    @NotBlank
    private String fullName;
    @NotNull
    private String emailId;
    @NotNull
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return this.emailId;
    }

    public enum Gender{
        MALE,
        FEMALE,
        PREFER_NOT_TO_SAY
    }

    public enum Role {
        USER,
        ADMIN
    }
}
