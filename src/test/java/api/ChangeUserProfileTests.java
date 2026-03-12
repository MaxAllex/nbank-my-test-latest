package api;

import api.dto.TestUser;
import api.dto.admin.CreateUserResponse;
import api.dto.customer.GetCustomerProfileResponse;
import api.generators.NameGenerator;
import api.generators.UserNameType;
import api.steps.DataBaseSteps;
import api.steps.customer.CustomerSteps;
import common.annotations.CreateUser;
import common.storage.SessionStorage;
import db.dao.UserDao;
import db.dao.comparison.DaoAndModelAssertions;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

@Tags(value = {@Tag("change_username"), @Tag("api")})
public class ChangeUserProfileTests extends BaseTests {

  @ParameterizedTest
  @CreateUser
  @EnumSource(value = UserNameType.class)
  @Tags({@Tag("positive"), @Tag("e2e")})
  public void changeUsernameSystemTest(UserNameType type) {
    String username = getDataSteps().generateTestUserName(type);
    TestUser user = users.getFirst();
    CustomerSteps customerSteps = getCustomerSteps(user);
    GetCustomerProfileResponse profile = customerSteps.getCustomerProfile();
    softly.assertThat(profile.getName()).isNullOrEmpty();
    customerSteps.updateCustomerProfile(username);
    profile = customerSteps.getCustomerProfile();
    softly.assertThat(profile.getName()).isEqualTo(username);

    UserDao userDao = DataBaseSteps.getUserByUsername(profile.getUsername());
    DaoAndModelAssertions.assertThat(profile, userDao).match();
  }

  @Test
  @Tags({@Tag("negative"), @Tag("admin_token")})
  public void tryChangeUsernameWithAdminTokenNegativeTest() {
    String name = NameGenerator.generate(UserNameType.VALID_LATIN);
    CustomerSteps customerSteps = SessionStorage.getAdminCustomerSteps();
    customerSteps.updateCustomerProfile(name, HttpStatus.SC_FORBIDDEN);
    boolean result = getAdminSteps().getUsers().stream().noneMatch(x -> name.equals(x.getName()));
    softly.assertThat(result).isTrue();

    CreateUserResponse user = getAdminSteps().getUsers().stream().findAny().get();
    UserDao userDao = DataBaseSteps.getUserById(user.getId());
    DaoAndModelAssertions.assertThat(user, userDao).match();
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {"Имя", "имя.", "full.name", "full_name"})
  @CreateUser
  @Tags({@Tag("negative"), @Tag("incorrect_data_name")})
  public void tryChangeUsernameWithIncorrectDataTest(String name) {
    TestUser user = users.getFirst();
    var customerSteps = getCustomerSteps(user);
    customerSteps.updateCustomerProfile(name, HttpStatus.SC_BAD_REQUEST);
    List<CreateUserResponse> users = getAdminSteps().getUsers();
    boolean result = users.stream().noneMatch(x -> name.equals(x.getName()));
    softly.assertThat(result).isTrue();

    CreateUserResponse findAdmin = users.stream().findAny().get();
    UserDao userDao = DataBaseSteps.getUserById(findAdmin.getId());
    DaoAndModelAssertions.assertThat(findAdmin, userDao).match();
  }
}
