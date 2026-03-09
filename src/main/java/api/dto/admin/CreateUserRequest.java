package api.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    String username;
    String password;
    String role;
}
