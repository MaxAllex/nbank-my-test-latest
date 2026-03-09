package api.dto.authentication;

import lombok.Data;

@Data
public class LoginResponse{
    String username;
    String role;
}
