package api;

import api.dto.TestUser;
import api.steps.DataBaseSteps;
import db.dao.AccountDao;
import db.dao.comparison.DaoAndModelAssertions;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import common.annotations.CreateUserWithAccount;

import static org.assertj.core.data.Offset.offset;

@Tags(value = {@Tag("transfer_money"), @Tag("api")})
public class TransferMoneyTests extends BaseTests {

    @ParameterizedTest
    @CreateUserWithAccount(howManyUsers = 2, amount = 10_000.00)
    @ValueSource(doubles = {250.75, 00.01, 10_000, 9_999.99})
    @Tags({@Tag("positive"), @Tag("e2e"), @Tag("to_other_account")})
    public void transferMoneyToOtherAccountTests(double transferAmount) {
        double amount = 10_000.00;
        TestUser userFirst = users.getFirst();
        TestUser userLast = users.getLast();

        getAccountsSteps(userFirst).transferMoney(
                userFirst.getAccounts().getFirst().getId(),
                userLast.getAccounts().getFirst().getId(),
                transferAmount
        );
        
        var userFirstAccounts = getCustomerSteps(userFirst).getCustomerAccounts();
        var userLastAccounts = getCustomerSteps(userLast).getCustomerAccounts();
        
        var userFirstAccount = userFirstAccounts.stream()
                .filter(account -> account.getId() == userFirst.getAccounts().getFirst().getId())
                .findFirst()
                .orElseThrow();
        var userLastAccount = userLastAccounts.stream()
                .filter(account -> account.getId() == userLast.getAccounts().getFirst().getId())
                .findFirst()
                .orElseThrow();

        double userFirstExpectedAmount = amount - transferAmount;
        double userLastExpectedAmount = amount + transferAmount;
        softly.assertThat(userFirstAccount.getBalance()).isCloseTo(userFirstExpectedAmount, offset(0.01));
        softly.assertThat(userLastAccount.getBalance()).isCloseTo(userLastExpectedAmount, offset(0.01));

        AccountDao accountDaoFirst = DataBaseSteps.getAccountByAccountNumber(userFirstAccounts.getFirst().getAccountNumber());
        AccountDao accountDaoLast = DataBaseSteps.getAccountByAccountNumber(userLastAccounts.getFirst().getAccountNumber());
        DaoAndModelAssertions.assertThat(userFirstAccounts.getFirst(), accountDaoFirst).match();
        DaoAndModelAssertions.assertThat(userLastAccounts.getFirst(), accountDaoLast).match();

    }

    @Test
    @CreateUserWithAccount(amount = 5000, howManyAccounts = 2)
    @Tags({@Tag("positive"), @Tag("e2e"), @Tag("to_my_account")})
    public void transferMoneyToMyAccountTests() {
        TestUser userFirst = users.getFirst();
        var transferAmount = 100;

        getAccountsSteps(userFirst).transferMoney(
                userFirst.getAccounts().getFirst().getId(),
                userFirst.getAccounts().getLast().getId(),
                transferAmount
        );
        
        var userFirstAccounts = getCustomerSteps(userFirst).getCustomerAccounts();
        
        var userFirstAccountFirst = userFirstAccounts.stream()
                .filter(account -> account.getId() == userFirst.getAccounts().getFirst().getId())
                .findFirst()
                .orElseThrow();
        var userFirstAccountLast = userFirstAccounts.stream()
                .filter(account -> account.getId() == userFirst.getAccounts().getLast().getId())
                .findFirst()
                .orElseThrow();

        double userFirstExpectedAccountFirst = userFirst.getAccounts().getFirst().getBalance() - transferAmount;
        double userFirstExpectedAccountLast = userFirst.getAccounts().getFirst().getBalance() + transferAmount;
        softly.assertThat(userFirstAccountFirst.getBalance()).isEqualTo(userFirstExpectedAccountFirst);
        softly.assertThat(userFirstAccountLast.getBalance()).isEqualTo(userFirstExpectedAccountLast);

        AccountDao accountDaoFirst = DataBaseSteps.getAccountByAccountNumber(userFirstAccounts.getFirst().getAccountNumber());
        AccountDao accountDaoLast = DataBaseSteps.getAccountByAccountNumber(userFirstAccounts.getLast().getAccountNumber());
        DaoAndModelAssertions.assertThat(userFirstAccounts.getFirst(), accountDaoFirst).match();
        DaoAndModelAssertions.assertThat(userFirstAccounts.getLast(), accountDaoLast).match();
    }

    @ParameterizedTest
    @CreateUserWithAccount(howManyUsers = 2, amount = 10_005)
    @ValueSource(doubles = {10_001, 10_000.01})
    @Tags({@Tag("negative"), @Tag("more_then_possible")})
    public void transferMoneyMoreThenPossibleTests(double amount) {
        TestUser userFirst = users.getFirst();
        TestUser userLast = users.getLast();
        double expectedAmount = 10_005;

        var accountsSteps = getAccountsSteps(userFirst);
        ValidatableResponse transferredMoney = accountsSteps.transferMoney(
                userFirst.getAccounts().getFirst().getId(),
                userLast.getAccounts().getFirst().getId(),
                amount,
                HttpStatus.SC_BAD_REQUEST
        );
        softly.assertThat(users.getFirst().getAccounts().stream().allMatch(a -> a.getBalance() == expectedAmount)).isTrue();
        softly.assertThat(users.getLast().getAccounts().stream().allMatch(a -> a.getBalance() == expectedAmount)).isTrue();

        AccountDao accountDaoFirst = DataBaseSteps.getAccountById(users.getFirst().getAccounts().getFirst().getId());
        softly.assertThat(accountDaoFirst.getBalance()).isEqualTo(expectedAmount);
    }

    @ParameterizedTest
    @CreateUserWithAccount(howManyUsers = 2, amount = 5000)
    @ValueSource(doubles = {5001, 5000.01, -1000})
    @Tags({@Tag("negative"), @Tag("more_then_is_in_balance")})
    public void transferMoreMoneyThenIsInBalanceNegativeTests(double amount) {
        TestUser userFirst = users.getFirst();
        TestUser userLast = users.getLast();

        getAccountsSteps(userFirst).transferMoney(
                userFirst.getAccounts().getFirst().getId(),
                userLast.getAccounts().getFirst().getId(),
                amount,
                HttpStatus.SC_BAD_REQUEST
        );

        softly.assertThat(users.getFirst().getAccounts().stream().allMatch(a -> a.getBalance() == MAX_DEPOSIT)).isTrue();
        softly.assertThat(users.getLast().getAccounts().stream().allMatch(a -> a.getBalance() == MAX_DEPOSIT)).isTrue();

        AccountDao accountDaoFirst = DataBaseSteps.getAccountById(users.getFirst().getAccounts().getFirst().getId());
        softly.assertThat(accountDaoFirst.getBalance()).isEqualTo(MAX_DEPOSIT);
    }

    @Test
    @CreateUserWithAccount(howManyUsers = 2, amount = 5000)
    @Tags({@Tag("negative"), @Tag("auth_by_admin")})
    public void transferMoneyByAdminNegativeTest() {
        TestUser userFirst = users.getFirst();
        TestUser userLast = users.getLast();

        getAdminAccountsSteps().transferMoneyAuthByToken(
                userFirst.getAccounts().getFirst().getId(),
                userLast.getAccounts().getFirst().getId(),
                1,
                HttpStatus.SC_FORBIDDEN
        );

        softly.assertThat(users.getFirst().getAccounts().stream().allMatch(a -> a.getBalance() == MAX_DEPOSIT)).isTrue();
        softly.assertThat(users.getLast().getAccounts().stream().allMatch(a -> a.getBalance() == MAX_DEPOSIT)).isTrue();

        AccountDao accountDaoFirst = DataBaseSteps.getAccountById(users.getFirst().getAccounts().getFirst().getId());
        softly.assertThat(accountDaoFirst.getBalance()).isEqualTo(MAX_DEPOSIT);
    }
}
