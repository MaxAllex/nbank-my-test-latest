package api.steps.accounts;

public class AccountsStepsFactory {

    public static AccountsSteps byCredentials(String username, String password) {
        return new AccountsStepsByAuth(username, password);
    }

    public static AccountsSteps byToken(String token) {
        return new AccountsStepsByToken(token);
    }
}
