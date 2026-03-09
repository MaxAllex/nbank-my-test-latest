package ui.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import ui.elements.RepeatTransferModal;
import ui.elements.TransactionRow;

import static com.codeborne.selenide.Selenide.$;

import java.util.List;

public class TransferMoneyPage extends BasePage<TransferMoneyPage> {
    private final SelenideElement accountSelector = $("select");
    private final SelenideElement recipientNameField = $("input[placeholder='Enter recipient name']");
    private final SelenideElement recipientAccountNumberField = $("input[placeholder='Enter recipient account number']");
    private final SelenideElement amountField = $("input[placeholder='Enter amount']");
    private final SelenideElement sendTransferButton = $(Selectors.byText("🚀 Send Transfer"));
    private final SelenideElement transferAgainButton = $("button[class='custom-btn shadow-custom gray-btn']");
    private final SelenideElement confirmDetailsCheckBox = $("#confirmCheck");
    private final SelenideElement searchField = $("input[placeholder='Enter name to find transactions']");
    private final SelenideElement searchButton = $("button[class='custom-btn shadow-custom blue-btn mt-3']");

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferMoneyPage selectAccountSender(String account) {
        accountSelector.$("option[value='" + account + "']")
                .shouldBe(Condition.exist);
        accountSelector.should(Condition.enabled)
                .selectOptionByValue(account);
        return this;
    }

    public TransferMoneyPage enterRecipientName(String recipientName) {
        recipientNameField.shouldHave(Condition.enabled)
                .setValue(recipientName);
        return this;
    }

    public TransferMoneyPage enterRecipientAccountNumber(String recipientAccountNumber) {
        recipientAccountNumberField.shouldHave(Condition.enabled)
                .setValue(recipientAccountNumber);
        return this;
    }

    public TransferMoneyPage enterAmount(String amount) {
        amountField.shouldHave(Condition.enabled)
                .setValue(amount);
        return this;
    }

    public TransferMoneyPage clickSendTransferButton() {
        sendTransferButton.shouldHave(Condition.enabled)
                .click();
        return this;
    }

    public TransferMoneyPage clickConfirmDetailsCheckBox() {
        confirmDetailsCheckBox.shouldHave(Condition.enabled)
                .click();
        return this;
    }

    public TransferMoneyPage clickTransferAgainButton() {
        transferAgainButton.shouldHave(Condition.enabled)
                .click();
        return this;
    }

    public List<TransactionRow> getAllTransactionRows() {
        ElementsCollection elementsCollection = $(".list-group").parent().findAll("li");
        elementsCollection.shouldBe(CollectionCondition.sizeGreaterThan(0));
        return generatePageElements(elementsCollection, TransactionRow::new);
    }

    public TransactionRow findFirstTransactionByType(String type) {
        return getAllTransactionRows().stream()
                .filter(row -> row.getTransactionType().equals(type))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError(
                                "Transaction with type [" + type + "] not found"
                        )
                );
    }

    public RepeatTransferModal repeatFirstTransactionByType(TransactionType type) {
        return findFirstTransactionByType(type.name())
                .clickRepeat();
    }

    public TransferMoneyPage searchTransactions(String searchText) {
        searchField.shouldHave(Condition.enabled).setValue(searchText);
        searchButton.shouldHave(Condition.enabled).click();
        com.codeborne.selenide.Selenide.$$(".list-group li")
                .shouldBe(CollectionCondition.sizeGreaterThan(0));
        return this;
    }
}
