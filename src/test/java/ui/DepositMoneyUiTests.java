package ui;

import api.dto.accounts.AccountResponse;
import api.steps.DataBaseSteps;
import common.annotations.CreateUserWithAccount;
import db.dao.AccountDao;
import db.dao.comparison.DaoAndModelAssertions;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboardPage;

public class DepositMoneyUiTests extends BaseUiTest {
  @Test
  @CreateUserWithAccount
  public void depositMoneyUiTest() {
    AccountResponse account = users.getFirst().getAccounts().getFirst();
    double amount = 5000.00;
    new UserDashboardPage()
        .open()
        .clickToDepositMoneyButton()
        .depositMoney(String.valueOf(account.getId()), String.valueOf(amount))
        .checkAlertMessageAndAccept(
            BankAlert.SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.getMessage(
                account.getAccountNumber(), amount));

    AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(account.getAccountNumber());
    DaoAndModelAssertions.assertThat(account, accountDao).match();
  }

  @Test
  @CreateUserWithAccount
  public void depositMoneyWithInvalidAmountUiTest() {
    AccountResponse account = users.getFirst().getAccounts().getFirst();
    double invalidAmount = -100.00;
    new UserDashboardPage()
        .open()
        .clickToDepositMoneyButton()
        .depositMoney(String.valueOf(account.getId()), String.valueOf(invalidAmount))
        .checkAlertMessageAndAccept(BankAlert.INVALID_AMOUNT.getMessage());
  }

  @Test
  @CreateUserWithAccount
  public void depositMoneyWithAmountIsMoreUiTest() {
    AccountResponse account = users.getFirst().getAccounts().getFirst();
    double invalidAmount = 5001;
    new UserDashboardPage()
        .open()
        .clickToDepositMoneyButton()
        .depositMoney(String.valueOf(account.getId()), String.valueOf(invalidAmount))
        .checkAlertMessageAndAccept(BankAlert.AMOUNT_IS_MORE.getMessage(invalidAmount));
  }

  @Test
  @CreateUserWithAccount(amount = 5000)
  public void depositedAmountVisibleUiTest() {
    AccountResponse account = users.getFirst().getAccounts().getFirst();
    double sumAmount = 5000;
    softly
        .assertThat(
            new UserDashboardPage()
                .open()
                .clickToDepositMoneyButton()
                .getAccountBalance(String.valueOf(account.getId()))
                .getText())
        .contains(String.valueOf(sumAmount));
  }
}
