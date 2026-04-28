package ui;

import api.generators.NameGenerator;
import api.generators.UserNameType;
import api.steps.DataBaseSteps;
import com.codeborne.selenide.Condition;
import common.annotations.CreateUserWithAccount;
import db.dao.UserDao;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboardPage;

public class ChangeUserProfileUiTests extends BaseUiTest {
  @Test
  @CreateUserWithAccount
  public void changeUsernameTest() {
    String newName = NameGenerator.generate(UserNameType.VALID_LATIN);
    UserDashboardPage userDashboardPage =
        new UserDashboardPage()
            .open()
            .clickToEditName()
            .changeName(newName)
            .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
            .goUserDashboardPage();
    userDashboardPage.refresh();
    softly
        .assertThat(
            userDashboardPage.getWelcomeText().shouldHave(Condition.text(newName)).getText())
        .contains(newName);
    softly
        .assertThat(
            userDashboardPage.getEditProfileSpan().shouldHave(Condition.text(newName)).getText())
        .contains(newName);

    UserDao userDao = DataBaseSteps.getUserByUsername(users.getFirst().getResponse().getUsername());
    softly.assertThat(userDao.getName()).isEqualTo(newName);
  }
}
