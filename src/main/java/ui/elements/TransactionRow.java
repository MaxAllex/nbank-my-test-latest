package ui.elements;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class TransactionRow extends BaseElement {

  private static final SelenideElement repeatButton =
      $(By.xpath(".//button[contains(., 'Repeat')]"));
  private final SelenideElement repeatModalRoot = $(".modal-dialog");

  public TransactionRow(SelenideElement element) {
    super(element);
  }

  public String getTransactionType() {
    return element.shouldBe(Condition.visible).getText().split(" ")[0].trim();
  }

  public RepeatTransferModal clickRepeat() {
    repeatButton.shouldBe(Condition.enabled).click();
    SelenideElement modalRoot = repeatModalRoot.shouldBe(Condition.visible);
    return new RepeatTransferModal(modalRoot).shouldBeOpened();
  }
}
