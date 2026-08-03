package io.angate.AnGate.dto.user;

import io.angate.AnGate.entity.Users;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String emailId;
    private Users.Role role;


}
