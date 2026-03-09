package api;

import api.dto.TestUser;
import api.dto.accounts.TransferResponse;
import api.dto.comparison.ModelAssertions;
import api.steps.accounts.AccountsSteps;
import common.annotations.CreateUserWithAccount;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import common.extensions.TimingExtension;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Random;

@ExtendWith({TimingExtension.class, FraudCheckWireMockExtension.class})
public class TransferWithFraudCheckTests extends BaseTests {
    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    @CreateUserWithAccount(howManyAccounts = 2, amount = 4900.80)
    public void testTransferWithFraudCheck() {
        TestUser user = users.getFirst();
        double transferAmount = 1.0 + (4899.79 - 1.0) * new Random().nextDouble();

        AccountsSteps accountsSteps = SessionStorage.getAccountsSteps(user);
        TransferResponse transferResponse = accountsSteps.transferWithFraudCheck(
                user.getAccounts().getFirst().getId(),
                user.getAccounts().getLast().getId(),
                transferAmount
        );

        softly.assertThat(accountsSteps).isNotNull();


        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(user.getAccounts().getFirst().getId())
                .receiverAccountId(user.getAccounts().getLast().getId())
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}
