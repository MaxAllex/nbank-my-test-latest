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
public class DepositMoneyRequest extends BaseDto {
    private Long accountId;
    private double amount;
}
