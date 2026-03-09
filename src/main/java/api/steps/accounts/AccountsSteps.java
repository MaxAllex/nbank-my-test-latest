package api.steps.accounts;

import api.dto.accounts.AccountResponse;
import api.dto.accounts.DepositMoneyResponse;
import api.dto.accounts.TransferMoneyResponse;
import api.dto.accounts.TransferResponse;
import io.restassured.response.ValidatableResponse;

public interface AccountsSteps {
    AccountResponse createAccount();
    AccountResponse depositMoney(long id, double amount);
    ValidatableResponse depositMoney(long id, double amount, int errorCode);
    ValidatableResponse depositMoneyAuthByToken(long id, double amount, int errorCode);
    TransferMoneyResponse transferMoney(long senderAccountId, long receiverAccountId, double amount);
    ValidatableResponse transferMoney(long senderAccountId, long receiverAccountId, double amount, int errorCode);
    ValidatableResponse transferMoneyAuthByToken(long senderAccountId, long receiverAccountId, double amount, int errorCode);
    TransferResponse transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount);
}
