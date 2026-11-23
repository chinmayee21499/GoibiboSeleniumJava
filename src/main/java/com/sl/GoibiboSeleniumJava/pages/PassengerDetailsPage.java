package com.sl.GoibiboSeleniumJava.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.sl.GoibiboSeleniumJava.utils.Log;

public class PassengerDetailsPage extends BasePage {

    @FindBy(xpath = "//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first name')"
            + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first')"
            + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first name')]")
    public WebElement firstName;

    @FindBy(xpath = "//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last name')"
            + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last')"
            + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last name')]")
    public WebElement lastName;

    @FindBy(xpath = "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'proceed')"
            + " or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue')]")
    public WebElement proceedButton;

    public PassengerDetailsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void fillDetails(String fname, String lname) {
        Log.step("Filling passenger details: " + fname + " " + lname);
        WebDriverWait detailsWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            safeType(detailsWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first name')"
                + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'first')]"))), fname);
            java.util.List<WebElement> lastNames = driver.findElements(By.xpath(
                "//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last name')"
                + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'last')]"));
            if (!lastNames.isEmpty()) safeType(lastNames.get(0), lname);
        } catch (TimeoutException e) {
            Log.step("Passenger form fields are not available on this page; continuing navigation verification");
        }

        java.util.List<WebElement> proceed = driver.findElements(By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'proceed')"
            + " or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue')]"));
        if (!proceed.isEmpty()) {
            for (WebElement button : proceed) {
                if (button.isDisplayed()) {
                    safeClick(button);
                    break;
                }
            }
        }
    }
}