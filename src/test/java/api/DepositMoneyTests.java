package api;

import api.dto.accounts.AccountResponse;
import api.dto.admin.CreateUserResponse;
import api.dto.customer.GetCustomerProfileResponse;
import api.steps.DataBaseSteps;
import db.dao.AccountDao;
import db.dao.comparison.DaoAndModelAssertions;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import common.annotations.CreateUser;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.data.Offset.offset;

@Tags(value = {@Tag("deposit_money"), @Tag("api")})
public class DepositMoneyTests extends BaseTests {
    @ParameterizedTest
    @ValueSource(doubles = {4999.99, 5000.00, 00.01})
    @CreateUser
    @Tags({@Tag("positive"), @Tag("e2e")})
    public void depositMoneyByUserTest(double amount) {
        var user = users.getFirst();
        var accountsSteps = getAccountsSteps(user);
        AccountResponse account = accountsSteps.createAccount();
        accountsSteps.depositMoney(account.getId(), amount);
        var customerSteps = getCustomerSteps(user);
        double actualBalance = customerSteps.getCustomerAccounts().getFirst().getBalance();
        softly.assertThat(actualBalance).isCloseTo(amount, offset(0.01));

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(customerSteps.getCustomerAccounts().getFirst().getAccountNumber());
        DaoAndModelAssertions.assertThat(customerSteps.getCustomerAccounts().getFirst(), accountDao).match();
    }

    @Test
    @Tags({@Tag("negative"), @Tag("deposit_money_by_admin")})
    public void depositMoneyByAdminNegativeTest() {
        double depositAmount = 100;
        var accountsSteps = getAdminAccountsSteps();
        accountsSteps.depositMoneyAuthByToken(1, depositAmount, HttpStatus.SC_FORBIDDEN);
        //У Админа не может быть созданного аккаунта. АПИ не позволяет проверить баланс админа
    }

    @Test
    @CreateUser(count = 2)
    @Tags({@Tag("negative"), @Tag("from_someone_else_account")})
    public void depositMoneyFromSomeoneElseAccountNegativeTest() {
        double depositAmount = 100;
        var firstUser = users.getFirst();
        var lastUser = users.getLast();
        getAccountsSteps(firstUser).depositMoney(lastUser.getResponse().getId(), depositAmount, HttpStatus.SC_FORBIDDEN);
        List<CreateUserResponse> users = getAdminSteps().getUsers().stream().filter(x -> x.getId() == firstUser.getResponse().getId() || x.getId() == lastUser.getResponse().getId()).toList();
        softly.assertThat(users).allMatch(x -> Arrays.stream(x.getAccounts()).noneMatch(a -> depositAmount == a.getBalance()));
    }

    @Test
    @CreateUser
    @Tags({@Tag("negative"), @Tag("on_non_existent_account")})
    public void depositMoneyOnNonExistentAccountNegativeTest() {
        double depositAmount = 100;
        long nonExistentId = users.stream().mapToLong(x -> x.getResponse().getId()).max().getAsLong() + 100L;
        var firstUser = users.getFirst();
        getAccountsSteps(firstUser).depositMoney(nonExistentId, depositAmount, HttpStatus.SC_FORBIDDEN);
        softly.assertThat(firstUser.getAccounts()).isNullOrEmpty();
    }

    @ParameterizedTest
    @CreateUser
    @ValueSource(doubles = {-100, -0.01})
    @Tags({@Tag("negative"), @Tag("negative_balance")})
    public void depositMoneyNegativeBalanceNegativeTest(double amount) {
        var firstUser = users.getFirst();
        getAccountsSteps(firstUser).depositMoney(firstUser.getResponse().getId(), amount, HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(firstUser.getAccounts()).isNullOrEmpty();
    }

    @ParameterizedTest
    @CreateUser
    @ValueSource(doubles = {5000.01, 5001.00})
    @Tags({@Tag("negative"), @Tag("negative_balance")})
    public void depositMoneyMoreBalanceNegativeTest(double amount) {
        var firstUser = users.getFirst();
        getAccountsSteps(firstUser).depositMoney(firstUser.getResponse().getId(), amount, HttpStatus.SC_FORBIDDEN);
        softly.assertThat(firstUser.getAccounts()).isNullOrEmpty();
    }
}
