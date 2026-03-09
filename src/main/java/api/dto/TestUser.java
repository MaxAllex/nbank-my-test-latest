package api.dto;

import api.dto.accounts.AccountResponse;
import api.dto.admin.CreateUserRequest;
import api.dto.admin.CreateUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TestUser {
    CreateUserRequest request;
    CreateUserResponse response;
    List<AccountResponse> accounts;
}
