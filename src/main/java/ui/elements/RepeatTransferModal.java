package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ui.pages.TransferMoneyPage;

import static com.codeborne.selenide.Selenide.$;

public class RepeatTransferModal extends BaseElement{
    private static final SelenideElement MODAL_CONTENT = $(".modal-content");
    private static final SelenideElement ACCOUNT_SELECTOR = $("select");
    private static final SelenideElement AMOUNT_FIELD = $("input[type = number]");
    private static final SelenideElement CONFIRM_DETAILS_CHECK_BOX = $("#confirmCheck");
    private static final SelenideElement SEND_TRANSFER_BUTTON = $("button[class = 'btn btn-success']");

    public RepeatTransferModal(SelenideElement root) {
        super(root);
    }

    public RepeatTransferModal shouldBeOpened() {
        element.shouldBe(Condition.visible);
        MODAL_CONTENT.shouldHave(Condition.text("Repeat Transfer"));
        return this;
    }

    public RepeatTransferModal selectSenderAccount(String valueOrVisibleText) {
        ACCOUNT_SELECTOR.shouldBe(Condition.enabled).selectOptionContainingText(valueOrVisibleText);
        return this;
    }

    public RepeatTransferModal setAmount(String amount) {
        AMOUNT_FIELD.shouldBe(Condition.enabled).clear();
        AMOUNT_FIELD.setValue(amount);
        return this;
    }

    public RepeatTransferModal confirmDetails() {
        SelenideElement cb = CONFIRM_DETAILS_CHECK_BOX.shouldBe(Condition.enabled);
        if (!cb.isSelected()) cb.click();
        return this;
    }

    public TransferMoneyPage sendTransfer() {
        SEND_TRANSFER_BUTTON.shouldBe(Condition.enabled)
                .click();
        return new TransferMoneyPage();
    }
}
