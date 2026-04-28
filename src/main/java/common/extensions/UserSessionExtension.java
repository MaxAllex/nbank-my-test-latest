package common.extensions;

import api.configs.AppConfig;
import api.dto.Role;
import api.dto.TestUser;
import api.dto.accounts.AccountResponse;
import api.steps.AdminSteps;
import api.steps.accounts.AccountsSteps;
import api.steps.accounts.AccountsStepsFactory;
import common.annotations.CreateName;
import common.annotations.CreateUser;
import common.annotations.CreateUserWithAccount;
import common.annotations.MakeTransfer;
import common.storage.SessionStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class UserSessionExtension implements BeforeEachCallback {
  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    SessionStorage.clear();
    extensionContext
        .getTestMethod()
        .ifPresent(
            method -> {
              boolean isUiTest = isUiTest(extensionContext);
              CreateUser createUser = method.getAnnotation(CreateUser.class);
              CreateUserWithAccount createUserWithAccount =
                  method.getAnnotation(CreateUserWithAccount.class);
              MakeTransfer makeTransfer = method.getAnnotation(MakeTransfer.class);
              CreateName createName = method.getAnnotation(CreateName.class);

              createUsers(createUser, isUiTest);
              createUsers(createUserWithAccount, isUiTest);
              makeTransfer(makeTransfer);
              createName(createName);
            });
  }

  private void createName(CreateName createName) {
    List<TestUser> users = SessionStorage.getUsers();
    if (createName != null) {
      users.forEach(
          user -> {
            SessionStorage.getCustomerSteps(user).updateCustomerProfile(createName.name());
          });
    }
  }

  private void makeTransfer(MakeTransfer makeTransfer) {
    List<TestUser> users = SessionStorage.getUsers();
    if (makeTransfer != null) {
      TestUser senderUser = users.get(makeTransfer.senderUser() - 1);
      TestUser receiverUser = users.get(makeTransfer.receiverUser() - 1);
      AccountResponse senderAccount =
          senderUser.getAccounts().get(makeTransfer.senderAccount() - 1);
      AccountResponse receiverAccount =
          receiverUser.getAccounts().get(makeTransfer.receiverAccount() - 1);
      SessionStorage.getAccountsSteps(senderUser)
          .transferMoney(senderAccount.getId(), receiverAccount.getId(), makeTransfer.amount());
    }
  }

  private void createUsers(CreateUser createUser, boolean authUi) {
    List<TestUser> users = new ArrayList<>();
    if (createUser != null) {
      int count = createUser.count();
      for (int i = 0; i < count; i++) {
        users.add(new AdminSteps().createUser(Map.of("role", Role.USER.name())));
      }
      SessionStorage.addUsers(users);
      if (authUi) {
        int authAsUser = createUser.auth();
        BasePage.authAsUser(SessionStorage.getUser(authAsUser));
      }
    }
  }

  private void createUsers(CreateUserWithAccount createUserWithAccount, boolean authUi) {
    List<TestUser> users = new ArrayList<>();
    if (createUserWithAccount != null) {
      int count = createUserWithAccount.howManyUsers();
      double amount = createUserWithAccount.amount();
      int account = createUserWithAccount.howManyAccounts();
      for (int i = 0; i < count; i++) {
        TestUser user = new AdminSteps().createUser(Map.of("role", Role.USER.name()));
        TestUser userWithAccount = getUserWithAccount(user, amount, account);
        users.add(userWithAccount);
      }
      SessionStorage.addUsers(users);
      if (authUi) {
        int authAsUser = createUserWithAccount.auth();
        BasePage.authAsUser(SessionStorage.getUser(authAsUser));
      }
    }
  }

  private static TestUser getUserWithAccount(TestUser user, double amount, int howManyAccounts) {
    var accountsSteps =
        AccountsStepsFactory.byCredentials(
            user.getRequest().getUsername(), user.getRequest().getPassword());
    List<AccountResponse> account = new ArrayList<>();
    for (int i = 0; i < howManyAccounts; i++) {
      AccountResponse accountFirst = accountsSteps.createAccount();
      account.add(depositMoney(accountFirst, accountsSteps, amount));
    }
    return new TestUser(user.getRequest(), user.getResponse(), account);
  }

  private static AccountResponse depositMoney(
      AccountResponse account, AccountsSteps accountsSteps, double amount) {
    final double MAX_DEPOSIT = Double.parseDouble(AppConfig.getProperty("maxDeposit"));
    AccountResponse response = null;

    if (amount <= 0) {
      return account;
    }

    double remaining = amount;

    while (remaining > 0) {
      double deposit = Math.min(remaining, MAX_DEPOSIT);
      response = accountsSteps.depositMoney(account.getId(), deposit);
      remaining -= deposit;
    }

    return response;
  }

  private static boolean isUiTest(ExtensionContext extensionContext) {
    Class<?> testClass = extensionContext.getRequiredTestClass();
    String packageName = testClass.getPackageName();
    return packageName.startsWith("ui");
  }
}
