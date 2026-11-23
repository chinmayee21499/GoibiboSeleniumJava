package com.sl.GoibiboSeleniumJava.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;
import com.sl.GoibiboSeleniumJava.utils.Log;

public class PaymentPage extends BasePage {

    @FindBy(xpath = "//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'card')"
            + " or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'card')]")
    public WebElement cardNumber;

    @FindBy(xpath = "//button[text()='PAY NOW']")
    public WebElement payNowButton;

    public PaymentPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void processPayment(String card) {
        Log.step("Processing payment with card ending in: " + card.substring(card.length()-4));
        java.util.List<WebElement> cards = driver.findElements(By.xpath(
            "//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'card')"
            + " or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'card')]"));
        if (!cards.isEmpty()) {
            safeType(cards.get(0), card);
        }
        java.util.List<WebElement> payButtons = driver.findElements(By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pay now')"
            + " or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'make payment')]"));
        if (!payButtons.isEmpty()) {
            safeClick(payButtons.get(0));
        } else {
            Log.step("Payment gateway fields are unavailable; payment step intentionally not completed");
        }
    }
}
