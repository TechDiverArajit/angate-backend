package io.angate.AnGate.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @Email(message = "Provide a valid email")
    private String emailId;

    @Size(min = 8 , message = " minimum 8 length password is required")
    private String password;
}
