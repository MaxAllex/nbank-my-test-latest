package ui.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

public class UserDashboardPage extends BasePage<UserDashboardPage> {
  private final SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
  private final SelenideElement editProfileSpan = $("span[class='user-name']");
  private final SelenideElement depositMoneyButton = $(Selectors.byText("💰 Deposit Money"));
  private final SelenideElement makeTransferButton = $(Selectors.byText("🔄 Make a Transfer"));

  @Override
  public String url() {
    return "/dashboard";
  }

  public EditProfilePage clickToEditName() {
    editProfileSpan.shouldHave(Condition.visible);
    editProfileSpan.click();
    return new EditProfilePage();
  }

  public DepositMoneyPage clickToDepositMoneyButton() {
    depositMoneyButton.shouldHave(Condition.visible);
    depositMoneyButton.click();
    return new DepositMoneyPage();
  }

  public TransferMoneyPage clickToMakeTransferButton() {
    makeTransferButton.shouldHave(Condition.visible);
    makeTransferButton.click();
    return new TransferMoneyPage();
  }

  public SelenideElement getWelcomeText() {
    welcomeText.shouldBe(Condition.visible, Condition.enabled);
    return welcomeText;
  }

  public SelenideElement getEditProfileSpan() {
    editProfileSpan.shouldBe(Condition.visible, Condition.enabled);
    return editProfileSpan;
  }
}
