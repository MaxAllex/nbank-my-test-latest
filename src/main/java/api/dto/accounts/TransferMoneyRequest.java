package api.dto.accounts;

import api.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferMoneyRequest extends BaseDto {
    long senderAccountId;
    long receiverAccountId;
    double amount;
}
