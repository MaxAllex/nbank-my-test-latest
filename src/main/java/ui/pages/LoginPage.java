package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    public SelenideElement buttonLogin = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public LoginPage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        buttonLogin.click();
        return this;
    }
}
