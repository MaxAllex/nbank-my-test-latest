package api.dto.admin;

import api.dto.BaseDto;
import api.dto.accounts.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
public class CreateUserResponse extends BaseDto {
  long id;
  String username;
  String password;
  String name;
  String role;
  Account[] accounts;
}
