package common.storage;

import api.dto.TestUser;
import api.specs.RequestSpecs;
import api.steps.AdminSteps;
import api.steps.DataSteps;
import api.steps.accounts.AccountsSteps;
import api.steps.accounts.AccountsStepsFactory;
import api.steps.customer.CustomerSteps;
import api.steps.customer.CustomerStepsFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SessionStorage {

  private static final ThreadLocal<SessionStorage> INSTANCE =
      ThreadLocal.withInitial(SessionStorage::new);

  private final Map<String, TestUser> usersByUsername = new LinkedHashMap<>();
  private final Map<String, CustomerSteps> customerStepsByUsername = new LinkedHashMap<>();
  private final Map<String, AccountsSteps> accountsStepsByUsername = new LinkedHashMap<>();

  // admin steps (ленивая инициализация)
  private volatile AdminSteps adminSteps;
  private volatile CustomerSteps adminCustomerSteps;
  private volatile AccountsSteps adminAccountsSteps;
  private volatile DataSteps dataSteps;

  private final Object adminInitLock = new Object();

  private SessionStorage() {}

  private static String usernameOf(TestUser user) {
    if (user == null) {
      throw new IllegalArgumentException("TestUser is null");
    }
    if (user.getRequest() == null) {
      throw new IllegalArgumentException("TestUser.request() is null for user=" + user);
    }
    String username = user.getRequest().getUsername();
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("username is null/blank for user=" + user);
    }
    return username;
  }

  /**
   * Добавляет пользователей и инициализирует их steps. Админские steps здесь не трогаем, они
   * инициализируются лениво в геттерах.
   */
  public static void addUsers(List<TestUser> users) {
    if (users == null) {
      throw new IllegalArgumentException("users list is null");
    }
    for (TestUser user : users) {
      String username = usernameOf(user);
      String password = user.getRequest().getPassword();
      if (password == null) {
        throw new IllegalArgumentException("password is null for username=" + username);
      }

      INSTANCE.get().usersByUsername.put(username, user);

      INSTANCE
          .get()
          .customerStepsByUsername
          .put(username, CustomerStepsFactory.byCredentials(username, password));

      INSTANCE
          .get()
          .accountsStepsByUsername
          .put(username, AccountsStepsFactory.byCredentials(username, password));
    }
  }

  private void ensureAdminInitialized() {
    if (adminSteps != null
        && adminCustomerSteps != null
        && adminAccountsSteps != null
        && dataSteps != null) {
      return;
    }

    if (adminSteps != null
        && adminCustomerSteps != null
        && adminAccountsSteps != null
        && dataSteps != null) {
      return;
    }

    String adminToken = RequestSpecs.adminToken;
    if (adminToken == null || adminToken.isBlank()) {
      throw new IllegalStateException(
          "RequestSpecs.adminToken is null/blank. "
              + "Initialize admin token BEFORE calling admin steps getters.");
    }

    adminSteps = new AdminSteps();
    adminCustomerSteps = CustomerStepsFactory.byToken(adminToken);
    adminAccountsSteps = AccountsStepsFactory.byToken(adminToken);
    dataSteps = new DataSteps();

    if (adminCustomerSteps == null) {
      throw new IllegalStateException("CustomerStepsFactory.byToken(adminToken) returned null");
    }
    if (adminAccountsSteps == null) {
      throw new IllegalStateException("AccountsStepsFactory.byToken(adminToken) returned null");
    }
  }

  public static void clear() {
    INSTANCE.get().usersByUsername.clear();
    INSTANCE.get().customerStepsByUsername.clear();
    INSTANCE.get().accountsStepsByUsername.clear();
    RequestSpecs.clearAuthHeaders();
    INSTANCE.get().adminSteps = null;
    INSTANCE.get().adminCustomerSteps = null;
    INSTANCE.get().adminAccountsSteps = null;
    INSTANCE.get().dataSteps = null;
  }

  public static List<TestUser> getUsers() {
    return new ArrayList<>(INSTANCE.get().usersByUsername.values());
  }

  public static TestUser getUser(int number) {
    if (number <= 0) {
      throw new IllegalArgumentException("number must be >= 1, actual=" + number);
    }
    List<TestUser> users = getUsers();
    int idx = number - 1;
    if (idx >= users.size()) {
      throw new IndexOutOfBoundsException(
          "Requested user #" + number + " but size is " + users.size());
    }
    return users.get(idx);
  }

  public static CustomerSteps getCustomerSteps(TestUser user) {
    String username = usernameOf(user);
    CustomerSteps steps = INSTANCE.get().customerStepsByUsername.get(username);
    if (steps == null) {
      throw new IllegalStateException(
          "No CustomerSteps found for username="
              + username
              + ". Did you call SessionStorage.addUsers(...) ?");
    }
    return steps;
  }

  public static AccountsSteps getAccountsSteps(TestUser user) {
    String username = usernameOf(user);
    AccountsSteps steps = INSTANCE.get().accountsStepsByUsername.get(username);
    if (steps == null) {
      throw new IllegalStateException(
          "No AccountsSteps found for username="
              + username
              + ". Did you call SessionStorage.addUsers(...) ?");
    }
    return steps;
  }

  public static AdminSteps getAdminSteps() {
    INSTANCE.get().ensureAdminInitialized();
    return INSTANCE.get().adminSteps;
  }

  public static CustomerSteps getAdminCustomerSteps() {
    INSTANCE.get().ensureAdminInitialized();
    return INSTANCE.get().adminCustomerSteps;
  }

  public static AccountsSteps getAdminAccountsSteps() {
    INSTANCE.get().ensureAdminInitialized();
    return INSTANCE.get().adminAccountsSteps;
  }

  public static DataSteps getDataSteps() {
    INSTANCE.get().ensureAdminInitialized();
    return INSTANCE.get().dataSteps;
  }
}
