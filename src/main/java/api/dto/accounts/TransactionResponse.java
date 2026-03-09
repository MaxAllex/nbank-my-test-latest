package api.dto.accounts;

import api.dto.BaseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse extends BaseDto {
    long id;
    double amount;
    Double amountAsDouble;
    String type;
    LocalDateTime timestamp;
    String timestampAsString;
    Object relatedAccount;
    long relatedAccountId;
}
