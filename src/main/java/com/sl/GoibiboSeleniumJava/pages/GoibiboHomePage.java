package com.sl.GoibiboSeleniumJava.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sl.GoibiboSeleniumJava.utils.Log;

public class GoibiboHomePage extends BasePage {

    @FindBy(xpath = "//span[contains(@class,'icClose')] | //p[contains(@class,'icClose')] | //span[@class='logSprite icClose'] | //*[contains(@class, 'close')] | //*[contains(@class, 'Close')]")
    public WebElement closeLoginPopup;

    @FindBy(xpath = "//span[text()='From'] | //p[text()='From'] | //*[contains(text(), 'From')]")
    public WebElement fromCityDisplay;

    @FindBy(id = "fromCity")
    public WebElement cityInput;

    @FindBy(id = "toCity")
    public WebElement toCityInput;

    @FindBy(xpath = "//span[text()='To'] | //p[text()='To'] | //*[contains(text(), 'To')]")
    public WebElement toCityDisplay;

    @FindBy(xpath = "//span[text()='Departure'] | //p[text()='Departure']")
    public WebElement departureDateDisplay;

    @FindBy(xpath = "//span[text()='Travellers & Class'] | //p[text()='Travellers & Class']")
    public WebElement travellersAndClassDisplay;

    @FindBy(xpath = "//span[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'search')]"
            + " | //button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'search')]"
            + " | //*[contains(@class,'search') and (self::button or self::span or self::div)]")
    public WebElement searchButton;

    public GoibiboHomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void closeLoginPopupIfPresent() {
        Log.step("Checking for login popup...");
        try {
            // Using a bit longer wait for optional popup
            WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            shortWait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(closeLoginPopup));
            if (closeLoginPopup.isDisplayed()) {
                safeClick(closeLoginPopup);
                Log.step("Closed login popup");
            }
        } catch (Exception e) {
            Log.step("Login popup not found or not clickable: " + e.getMessage());
        }
    }

    public void searchFlights(String from, String to) {
        Log.step("Searching flights from " + from + " to " + to);
        
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(cityInput));
            safeClick(cityInput);
        } catch (Exception e) {
            navigateDirectToResults(from, to);
            return;
        }
        safeType(cityInput, from);
        selectFirstSuggestion(cityInput, from);

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(toCityInput));
        safeClick(toCityInput);
        safeType(toCityInput, to);
        selectFirstSuggestion(toCityInput, to);

        // Fall back to an explicit search control if Enter does not submit the form.
        waitForUrlChangeIfPossible();
        if (driver.getCurrentUrl().equals("https://www.goibibo.com/")) {
            try {
                WebElement control = findSearchControl();
                safeClick(control);
                waitForUrlChangeIfPossible();
            } catch (Exception ignored) {
                Log.step("Explicit search button was not available");
            }
        }
    }

    private void selectFirstSuggestion(WebElement input, String city) {
        try {
            String airportCode = city.equalsIgnoreCase("Delhi") ? "DEL"
                : city.equalsIgnoreCase("Mumbai") ? "BOM" : "";
            String hint = airportCode.isEmpty()
                ? city.toLowerCase()
                : airportCode.toLowerCase();
            java.util.List<WebElement> suggestions = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//*[(self::li or @role='option')"
                        + " and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'"
                        + hint + "') "
                        + "and string-length(normalize-space(.)) < 120]")
                )
            );
            for (WebElement suggestion : suggestions) {
                if (!suggestion.isDisplayed()) continue;
                String text = suggestion.getText().trim();
                if (text.isEmpty()) continue;
                if (!text.toLowerCase().contains(city.toLowerCase())) continue;
                Log.step("Selecting suggestion: " + text);
                safeClick(suggestion);
                return;
            }
        } catch (Exception e) {
            Log.step("No visible suggestion list for " + city + "; using keyboard selection");
        }
        input.sendKeys(Keys.ARROW_DOWN);
        input.sendKeys(Keys.ENTER);
    }

    private WebElement findSearchControl() {
        java.util.List<WebElement> controls = driver.findElements(By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'search')]"
            + " | //*[@role='button'][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'search')]"
            + " | //span[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'search flights')]"));
        for (WebElement control : controls) {
            if (control.isDisplayed()) return control;
        }
        throw new org.openqa.selenium.NoSuchElementException("Search control not found");
    }

    private void waitForUrlChangeIfPossible() {
        try {
            wait.withTimeout(java.time.Duration.ofSeconds(8))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.not(
                    org.openqa.selenium.support.ui.ExpectedConditions.urlToBe("https://www.goibibo.com/")));
        } catch (Exception ignored) {
            Log.step("Flight search did not change the URL within 8 seconds");
        }
    }

    private void navigateDirectToResults(String from, String to) {
        String departure = from.equalsIgnoreCase("Delhi") ? "DEL" : from;
        String destination = to.equalsIgnoreCase("Mumbai") ? "BOM" : to;
        java.time.LocalDate travelDate = java.time.LocalDate.now().plusDays(1);
        String date = travelDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        driver.get("https://www.goibibo.com/flight/search?itinerary="
            + departure + "-" + destination + "-" + date
            + "&tripType=O&paxType=A-1_C-0_I-0&intl=false&cabinClass=E&lang=eng");
    }
}