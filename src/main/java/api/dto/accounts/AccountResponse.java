package api.dto.accounts;

import api.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse extends BaseDto {
  private long id;
  private String accountNumber;
  private double balance;
  private List<TransactionResponse> transactions;
  private double depositAmount;
  private long transactionId;
}
