package ui.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

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
