package api.dto.accounts;

import api.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
public class Account extends BaseDto {

    long id;
    String accountNumber;
    double balance;
    List<Transaction> transactions;
}
