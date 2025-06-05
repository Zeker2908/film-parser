package ru.zeker.filmparser.config;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ParserConfig {

    private final ParserProperties parserProperties;

    @Bean(destroyMethod = "quit")
    public WebDriver driver() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium-browser");
        options.addArguments("--headless", "--no-sandbox", "--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=" + parserProperties.getUserAgents().get((int) (Math.random() * parserProperties.getUserAgents().size())));

        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        return new ChromeDriver(options);
    }
}
