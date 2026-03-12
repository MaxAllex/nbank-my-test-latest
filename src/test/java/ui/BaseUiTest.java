package ui;

import api.BaseTests;
import api.configs.AppConfig;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseUiTest extends BaseTests {

  @BeforeAll
  public static void setupSelenoid() {
    Configuration.remote = AppConfig.getProperty("uiRemote");
    Configuration.baseUrl = AppConfig.getProperty("uiBaseUrl");
    Configuration.browser = AppConfig.getProperty("uiBrowser");
    Configuration.browserSize = AppConfig.getProperty("uiBrowserSize");
    Configuration.screenshots = true;
    Configuration.savePageSource = true;
    Configuration.headless = true;
    Configuration.browserCapabilities.setCapability(
        "selenoid:options",
        Map.of(
            "enableVNC", true,
            "enableLog", true,
            "videoName", "video.mp4"));
  }

  @AfterEach
  public void closeBrowser() {
    Selenide.closeWebDriver();
  }
}
