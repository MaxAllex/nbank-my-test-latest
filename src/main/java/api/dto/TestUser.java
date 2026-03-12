package api.dto;

import api.dto.accounts.AccountResponse;
import api.dto.admin.CreateUserRequest;
import api.dto.admin.CreateUserResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestUser {
  CreateUserRequest request;
  CreateUserResponse response;
  List<AccountResponse> accounts;
}
