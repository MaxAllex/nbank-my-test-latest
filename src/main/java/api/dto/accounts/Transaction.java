package api.dto.accounts;

import api.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
public class Transaction extends BaseDto {
  long id;
  double amount;
  Double amountAsDouble;
  String type;
  LocalDateTime timestamp;
  String timestampAsString;
  Object relatedAccount;
  Integer relatedAccountId;
}
