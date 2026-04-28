package ui.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public class DepositMoneyPage extends BasePage<DepositMoneyPage> {
  private SelenideElement accountSelector = $("select.account-selector");
  private SelenideElement enterAmountField = $("input[placeholder='Enter amount']");
  private SelenideElement depositButton = $("button[class$='mt-4']");

  @Override
  public String url() {
    return "/deposit";
  }

  public DepositMoneyPage depositMoney(String accountId, String amount) {
    accountSelector.shouldHave(Condition.enabled).selectOptionByValue(accountId);
    enterAmountField.shouldBe(Condition.visible).setValue(amount);
    depositButton.click();
    return this;
  }

  public SelenideElement getAccountBalance(String accountId) {
    return accountSelector.$$("option").findBy(Condition.attribute("value", accountId));
  }
}
