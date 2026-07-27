package io.angate.AnGate.dto.user;

import io.angate.AnGate.entity.Users;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequest {

    private String fullName;
    @Email
    private String emailId;
    private String password;
    private Users.Gender gender;

}
