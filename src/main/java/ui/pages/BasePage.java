package ui.pages;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

import api.dto.TestUser;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

public abstract class BasePage<T extends BasePage> {
  protected SelenideElement usernameInput = $("input[placeholder='Username']");
  protected SelenideElement passwordInput = $("input[placeholder='Password']");

  public abstract String url();

  public T open() {
    return Selenide.open(url(), (Class<T>) this.getClass());
  }

  public void refresh() {
    Selenide.refresh();
  }

  public <T extends BasePage> T getPage(Class<T> pageClass) {
    return Selenide.page(pageClass);
  }

  public T checkAlertMessageAndAccept(String bankAlert) {
    Alert alert = switchTo().alert();
    assertThat(alert.getText().contains(bankAlert));
    alert.accept();
    return (T) this;
  }

  public static void authAsUser(String username, String password) {
    Selenide.open("/login");
    String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    Selenide.refresh();
  }

  public static void authAsUser(TestUser createUserRequest) {
    authAsUser(
        createUserRequest.getRequest().getUsername(), createUserRequest.getRequest().getPassword());
  }

  protected <T extends BaseElement> List<T> generatePageElements(
      ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
    List<T> elements = new ArrayList<>();
    for (SelenideElement element : elementsCollection.asDynamicIterable()) {
      elements.add(constructor.apply(element));
    }
    return elements;
  }
}
