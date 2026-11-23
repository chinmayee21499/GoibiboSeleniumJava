package com.sl.GoibiboSeleniumJava.testcases;

import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.sl.GoibiboSeleniumJava.factory.DriverFactory;
import com.sl.GoibiboSeleniumJava.utils.ConfigReader;

public class BaseTest {
	
	protected DriverFactory driverFactory;
	protected WebDriver driver;
	protected Properties prop;
	
	@BeforeMethod(alwaysRun = true)
	@Parameters({"browser", "headless"})
	public void setUp(@Optional String browser, @Optional String headless) {
		
		ConfigReader config = new ConfigReader();
		prop = config.initProp();
		
		String br = (browser != null && !browser.isEmpty() ? browser : prop.getProperty("browser", "chrome"));
		boolean isHeadless = (headless != null)
				? Boolean.parseBoolean(headless)
				: Boolean.parseBoolean(prop.getProperty("headless", "false"));
		
		driverFactory = new DriverFactory();
		driver = driverFactory.initDriver(br, isHeadless);
		try {
			driver.get(prop.getProperty("baseUrl"));
		} catch (TimeoutException e) {
			if (!driver.getCurrentUrl().contains("goibibo.com")) {
				throw e;
			}
		}

		WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(20));
		try {
			pageWait.until(ExpectedConditions.presenceOfElementLocated(By.id("fromCity")));
		} catch (TimeoutException e) {
			driver.navigate().refresh();
			try {
				pageWait.until(ExpectedConditions.presenceOfElementLocated(By.id("fromCity")));
			} catch (TimeoutException retryError) {
				// Search flow will report the exact missing UI if the live page remains unavailable.
			}
		}
		
	}
	
	@AfterMethod(alwaysRun = true)
	public void tearDown() {		
		if (driverFactory != null) driverFactory.quitDriver();
	}

}