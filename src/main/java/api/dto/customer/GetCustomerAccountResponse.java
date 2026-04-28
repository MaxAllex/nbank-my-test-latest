package api.dto.customer;

import api.dto.BaseDto;
import api.dto.accounts.Transaction;
import java.util.List;
import lombok.Data;

@Data
public class GetCustomerAccountResponse extends BaseDto {
  long id;
  String accountNumber;
  double balance;
  List<Transaction> transactions;
}
