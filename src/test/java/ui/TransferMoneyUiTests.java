package ui;

import api.dto.TestUser;
import api.dto.accounts.AccountResponse;
import api.dto.admin.CreateUserRequest;
import api.dto.customer.GetCustomerProfileResponse;
import api.steps.DataBaseSteps;
import common.annotations.CreateName;
import common.annotations.CreateUserWithAccount;
import common.annotations.MakeTransfer;
import db.dao.AccountDao;
import java.util.List;
import org.junit.jupiter.api.Test;
import ui.elements.TransactionRow;
import ui.pages.BankAlert;
import ui.pages.TransactionType;
import ui.pages.TransferMoneyPage;
import ui.pages.UserDashboardPage;

public class TransferMoneyUiTests extends BaseUiTest {

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10_000.00)
  public void transferMoneyTest() {
    double amount = 10_000.00;
    AccountResponse sender = users.getFirst().getAccounts().getFirst();
    TestUser receiver = users.getLast();

    new UserDashboardPage()
        .open()
        .clickToMakeTransferButton()
        .selectAccountSender(String.valueOf(sender.getId()))
        .enterRecipientName(receiver.getResponse().getName())
        .enterRecipientAccountNumber(receiver.getAccounts().getFirst().getAccountNumber())
        .enterAmount(String.valueOf(amount))
        .clickConfirmDetailsCheckBox()
        .clickSendTransferButton()
        .checkAlertMessageAndAccept(
            BankAlert.TRANSFER_MONEY_SUCCESSFULLY.getMessage(
                amount, receiver.getAccounts().getFirst().getAccountNumber()));

    AccountDao receiverAccountDaoFirst =
        DataBaseSteps.getAccountByAccountNumber(
            receiver.getAccounts().getFirst().getAccountNumber());
    AccountDao senderAccountDaoLast =
        DataBaseSteps.getAccountByAccountNumber(sender.getAccountNumber());
    softly.assertThat(receiverAccountDaoFirst.getBalance()).isEqualTo(amount + amount);
    softly.assertThat(senderAccountDaoLast.getBalance()).isEqualTo(0.0);
  }

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10_001.00)
  public void transferMoneyWithInvalidAmountTest() {
    double amount = 10_001.00;
    AccountResponse sender = users.getFirst().getAccounts().getFirst();
    TestUser receiver = users.getLast();

    new UserDashboardPage()
        .open()
        .clickToMakeTransferButton()
        .selectAccountSender(String.valueOf(sender.getId()))
        .enterRecipientName(receiver.getResponse().getName())
        .enterRecipientAccountNumber(receiver.getAccounts().getFirst().getAccountNumber())
        .enterAmount(String.valueOf(amount))
        .clickConfirmDetailsCheckBox()
        .clickSendTransferButton()
        .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_CANNOT_EXCEED_10000.getMessage());
  }

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10_001.00)
  public void transferMoneyWithNotFilledAmountTest() {
    double amount = 10_000.00;
    AccountResponse sender = users.getFirst().getAccounts().getFirst();
    TestUser receiver = users.getLast();

    new UserDashboardPage()
        .open()
        .clickToMakeTransferButton()
        .selectAccountSender(String.valueOf(sender.getId()))
        .enterRecipientName(receiver.getResponse().getName())
        .enterRecipientAccountNumber(receiver.getAccounts().getFirst().getAccountNumber())
        .enterAmount(String.valueOf(amount))
        .clickSendTransferButton()
        .checkAlertMessageAndAccept(
            BankAlert.TRANSFER_AMOUNT_FILL_ALL_FIELDS_AND_CONFIRM.getMessage());
  }

  @Test
  @CreateUserWithAccount(howManyAccounts = 2, amount = 10000.00)
  @MakeTransfer
  public void transferMoneyRepeatedTransferTest() {
    double amount = 1.00;
    AccountResponse sender = users.getFirst().getAccounts().getFirst();
    AccountResponse receiver = users.getFirst().getAccounts().getLast();

    TransferMoneyPage page =
        new UserDashboardPage().open().clickToMakeTransferButton().clickTransferAgainButton();

    List<TransactionRow> transactions = page.getAllTransactionRows().stream().toList();
    int expectedTransactionInCount =
        Math.toIntExact(
            transactions.stream()
                .filter(x -> x.getTransactionType().equals(TransactionType.TRANSFER_IN.name()))
                .count());
    int expectedTransactionOutCount =
        Math.toIntExact(
            transactions.stream()
                .filter(x -> x.getTransactionType().equals(TransactionType.TRANSFER_OUT.name()))
                .count());

    page.repeatFirstTransactionByType(TransactionType.TRANSFER_IN)
        .selectSenderAccount(sender.getAccountNumber())
        .setAmount(Double.toString(amount))
        .confirmDetails()
        .sendTransfer()
        .checkAlertMessageAndAccept(
            BankAlert.TRANSFER_AGAIN_SUCCESSFULLY.getMessage(
                amount, sender.getId(), receiver.getId()));

    List<TransactionRow> actualTransactions =
        new TransferMoneyPage().open().clickTransferAgainButton().getAllTransactionRows();
    int actualTransactionInCount =
        Math.toIntExact(
            actualTransactions.stream()
                .filter(x -> x.getTransactionType().equals(TransactionType.TRANSFER_IN.name()))
                .count());
    int actualTransactionOutCount =
        Math.toIntExact(
            actualTransactions.stream()
                .filter(x -> x.getTransactionType().equals(TransactionType.TRANSFER_OUT.name()))
                .count());

    softly.assertThat(actualTransactions).hasSize(transactions.size() + 2);
    softly.assertThat(actualTransactionInCount).isEqualTo(expectedTransactionInCount + 1);
    softly.assertThat(actualTransactionOutCount).isEqualTo(expectedTransactionOutCount + 1);
  }

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10000.00)
  @MakeTransfer(receiverUser = 2, senderAccount = 1)
  public void searchBySenderUsernameTest() {
    CreateUserRequest sender = users.getFirst().getRequest();

    softly
        .assertThat(
            new UserDashboardPage()
                    .open()
                    .clickToMakeTransferButton()
                    .clickTransferAgainButton()
                    .searchTransactions(sender.getUsername())
                    .getAllTransactionRows()
                    .stream()
                    .filter(r -> r.getTransactionType().equals(TransactionType.TRANSFER_OUT.name()))
                    .count())
        .isOne();
  }

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10000.00, auth = 2)
  @MakeTransfer(receiverUser = 2, senderAccount = 1)
  public void searchByReceiverUsernameTest() {
    CreateUserRequest receiver = users.getLast().getRequest();

    softly
        .assertThat(
            new UserDashboardPage()
                    .open()
                    .clickToMakeTransferButton()
                    .clickTransferAgainButton()
                    .searchTransactions(receiver.getUsername())
                    .getAllTransactionRows()
                    .stream()
                    .filter(r -> r.getTransactionType().equals(TransactionType.TRANSFER_IN.name()))
                    .count())
        .isOne();
  }

  @Test
  @CreateUserWithAccount(howManyUsers = 2, amount = 10000.00)
  @MakeTransfer(receiverUser = 2, senderAccount = 1)
  @CreateName(name = "new name")
  public void searchByNameTest() {
    TestUser sender = users.getFirst();
    GetCustomerProfileResponse profile = getCustomerSteps(sender).getCustomerProfile();

    softly
        .assertThat(
            new UserDashboardPage()
                    .open()
                    .clickToMakeTransferButton()
                    .clickTransferAgainButton()
                    .searchTransactions(profile.getName())
                    .getAllTransactionRows()
                    .stream()
                    .filter(r -> r.getTransactionType().equals(TransactionType.TRANSFER_OUT.name()))
                    .count())
        .isOne();
  }
}
