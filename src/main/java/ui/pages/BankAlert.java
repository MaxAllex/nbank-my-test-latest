package ui.pages;

public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_UPDATED_BAD_REQUEST("Name must contain two words with letters only"),
    NAME_UPDATED_ERROR_SERVER_RESPONSE("❌ Please enter a valid name."),
    SUCCESSFULLY_DEPOSITED_TO_ACCOUNT("✅ Successfully deposited $%s to account %s!"),
    INVALID_AMOUNT("❌ Please enter a valid amount."),
    AMOUNT_IS_MORE("❌ Please deposit less or equal to %s$."),
    TRANSFER_MONEY_SUCCESSFULLY("✅ Successfully transferred $%s to account %s!"),
    TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    TRANSFER_AMOUNT_FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    TRANSFER_AGAIN_SUCCESSFULLY("✅ Transfer of $%s successful from Account %s to %s!");
    private final String template;

    BankAlert(String template) {
        this.template = template;
    }

    public String getMessage() {
        return template;
    }

    public String getMessage(Object... args) {
        return String.format(template, args);
    }
}
