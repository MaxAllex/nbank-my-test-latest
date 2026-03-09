package api.dto.customer;

import api.dto.accounts.Account;
import lombok.Data;

@Data
public class Customer {
    long id;
    String username;
    String password;
    String name;
    String role;
    Account[] accounts;
}
