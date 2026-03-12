package api.dto.accounts;

import api.dto.BaseDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class TransferMoneyResponse extends BaseDto {
  long receiverAccountId;
  double amount;
  String message;
  long senderAccountId;
}
