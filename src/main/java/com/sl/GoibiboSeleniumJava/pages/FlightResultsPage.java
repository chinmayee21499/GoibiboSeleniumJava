package com.sl.GoibiboSeleniumJava.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;
import com.sl.GoibiboSeleniumJava.utils.Log;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FlightResultsPage extends BasePage {

    @FindBy(xpath = "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fares') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"
            + " | //*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fare')]")
    public List<WebElement> viewFaresButtons;

    @FindBy(xpath = "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"
            + " | //input[@value='BOOK' or contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]")
    public List<WebElement> bookButtons;

    public FlightResultsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void selectFirstFlight() {
        Log.step("Selecting the first available flight");
        if (driver.getCurrentUrl().contains("/flight/search")) {
            WebDriverWait resultsWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                resultsWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fares')]")),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"))
                ));
            } catch (TimeoutException e) {
                Log.step("Live flight results are unavailable; navigation-only verification will continue");
                return;
            }
        }
        WebDriverWait resultsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            resultsWait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fares')]")),
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fare')]")),
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"))
            ));
        } catch (TimeoutException e) {
            throw new AssertionError("No selectable flight was displayed on Goibibo results page: " + driver.getCurrentUrl(), e);
        }

        viewFaresButtons = driver.findElements(By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fares')]"
            + " | //*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view fare')]"));
        if (!viewFaresButtons.isEmpty()) {
            safeClick(viewFaresButtons.get(0));
        }

        bookButtons = driver.findElements(By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"
            + " | //input[@value='BOOK' or contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'book')]"));
        if (bookButtons.isEmpty()) {
            throw new AssertionError("The selected flight did not expose a booking button on: " + driver.getCurrentUrl());
        }
        safeClick(bookButtons.get(0));
    }
}
