package api;

import api.configs.AppConfig;
import api.dto.TestUser;
import api.specs.RequestSpecs;
import api.steps.AdminSteps;
import api.steps.DataSteps;
import api.steps.accounts.AccountsSteps;
import api.steps.customer.CustomerSteps;
import common.extensions.TimingExtension;
import common.extensions.UserSessionExtension;
import common.storage.SessionStorage;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserSessionExtension.class)
@ExtendWith(TimingExtension.class)
public abstract class BaseTests {
  protected SoftAssertions softly = new SoftAssertions();
  protected List<TestUser> users;
  protected String adminToken;
  protected final double MAX_DEPOSIT = Double.parseDouble(AppConfig.getProperty("maxDeposit"));

  protected CustomerSteps getCustomerSteps(TestUser user) {
    return SessionStorage.getCustomerSteps(user);
  }

  protected AccountsSteps getAccountsSteps(TestUser user) {
    return SessionStorage.getAccountsSteps(user);
  }

  protected AdminSteps getAdminSteps() {
    return SessionStorage.getAdminSteps();
  }

  protected CustomerSteps getAdminCustomerSteps() {
    return SessionStorage.getAdminCustomerSteps();
  }

  protected AccountsSteps getAdminAccountsSteps() {
    return SessionStorage.getAdminAccountsSteps();
  }

  protected DataSteps getDataSteps() {
    return SessionStorage.getDataSteps();
  }

  @BeforeEach
  void setup() {
    this.users = SessionStorage.getUsers();
    this.adminToken = RequestSpecs.adminToken;
  }

  @AfterEach
  void postcondition() {
    AdminSteps steps = new AdminSteps();
    try {
      softly.assertAll();
    } finally {
      SessionStorage.getUsers().forEach(x -> steps.deleteUser(x.getResponse().getId()));
      SessionStorage.clear();
    }
  }
}
