package ui;

import api.dto.TestUser;
import api.dto.accounts.AccountResponse;
import api.dto.admin.CreateUserRequest;
import api.dto.customer.GetCustomerProfileResponse;
import api.steps.DataBaseSteps;
import common.annotations.CreateName;
import common.annotations.CreateUserWithAccount;
import db.dao.AccountDao;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ui.elements.TransactionRow;
import ui.pages.BankAlert;
import ui.pages.BasePage;
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
  @Disabled("Transfer history is empty in current frontend build")
  public void transferMoneyRepeatedTransferTest() {
    double amount = 1.00;
    TestUser senderUser = users.getFirst();
    AccountResponse sender = senderUser.getAccounts().getFirst();
    AccountResponse receiver = senderUser.getAccounts().getLast();
    createTransferViaUi(senderUser, sender, senderUser, receiver, amount);

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
  @Disabled("Transfer history search is unavailable in current frontend build")
  public void searchBySenderUsernameTest() {
    createTransferViaUi(
        users.getFirst(),
        users.getFirst().getAccounts().getFirst(),
        users.getLast(),
        users.getLast().getAccounts().getFirst(),
        100.00);
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
  @Disabled("Transfer history search is unavailable in current frontend build")
  public void searchByReceiverUsernameTest() {
    createTransferViaUi(
        users.getFirst(),
        users.getFirst().getAccounts().getFirst(),
        users.getLast(),
        users.getLast().getAccounts().getFirst(),
        100.00);
    BasePage.authAsUser(users.getLast());
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
  @CreateName(name = "new name")
  @Disabled("Transfer history search is unavailable in current frontend build")
  public void searchByNameTest() {
    TestUser sender = users.getFirst();
    createTransferViaUi(
        users.getFirst(),
        users.getFirst().getAccounts().getFirst(),
        users.getLast(),
        users.getLast().getAccounts().getFirst(),
        100.00);
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

  private void createTransferViaUi(
      TestUser senderUser,
      AccountResponse senderAccount,
      TestUser receiverUser,
      AccountResponse receiverAccount,
      double amount) {
    BasePage.authAsUser(senderUser);
    new UserDashboardPage()
        .open()
        .clickToMakeTransferButton()
        .selectAccountSender(String.valueOf(senderAccount.getId()))
        .enterRecipientName(receiverUser.getResponse().getName())
        .enterRecipientAccountNumber(receiverAccount.getAccountNumber())
        .enterAmount(String.valueOf(amount))
        .clickConfirmDetailsCheckBox()
        .clickSendTransferButton()
        .checkAlertMessageAndAccept(
            BankAlert.TRANSFER_MONEY_SUCCESSFULLY.getMessage(amount, receiverAccount.getAccountNumber()));
  }
}
