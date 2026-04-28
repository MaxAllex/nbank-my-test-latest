package ui.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public class EditProfilePage extends BasePage<EditProfilePage> {
  private final SelenideElement enterNewNameField = $("input[placeholder='Enter new name']");
  private final SelenideElement saveChangesButton = $("button[class$='mt-3']");
  private final SelenideElement goHomeButton =
      $("button[class='btn btn-outline-primary position-fixed']");

  @Override
  public String url() {
    return "/edit-profile";
  }

  public EditProfilePage changeName(String newName) {
    enterNewNameField.shouldBe(Condition.visible, Condition.enabled);
    enterNewNameField.clear();
    enterNewNameField.setValue(newName);

    // дождаться, что кнопка реально кликабельна (не перекрыта и не в процессе ререндера)
    saveChangesButton
        .shouldBe(Condition.visible, Condition.enabled)
        .scrollTo()
        .shouldBe(Condition.interactable)
        .click();
    return this;
  }

  public UserDashboardPage goUserDashboardPage() {
    goHomeButton.shouldBe(Condition.enabled).click();
    return new UserDashboardPage();
  }
}
