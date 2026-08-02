package io.angate.AnGate.dto.user;

import io.angate.AnGate.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @Pattern(
            regexp = ".*\\S.*",
            message = "Name cannot be blank"
    )
    @NotBlank
    private String fullName;
    @Email(message = "provide a valid email")
    private String emailId;
    @Size(min = 8 , message = "Minimum 8 length password is required")
    private String password;

}
