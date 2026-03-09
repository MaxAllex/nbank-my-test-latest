package api.dto.accounts;

import api.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest extends BaseDto {
    private Long senderAccountId;
    private Long receiverAccountId;
    private double amount;
    private String description;
}