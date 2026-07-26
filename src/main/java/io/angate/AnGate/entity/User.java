package io.angate.AnGate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

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
    @Enumerated(EnumType.ORDINAL)
    private Role role = Role.USER;

    private Boolean isActive;

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
