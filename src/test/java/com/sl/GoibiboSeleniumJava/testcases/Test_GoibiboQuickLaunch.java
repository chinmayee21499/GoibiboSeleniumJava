package com.sl.GoibiboSeleniumJava.testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sl.GoibiboSeleniumJava.pages.DashboardPage;
import com.sl.GoibiboSeleniumJava.pages.LoginPage;

public class Test_GoibiboQuickLaunch extends BaseTest {
	
	@Test(description = "Valid the web site GOIBIBO is launched")
	public void test() {
		LoginPage login = new LoginPage(driver);	

		
		//Validation page title
		Assert.assertTrue(driver.getTitle().toLowerCase().contains("goibibo"), "Page title does not contain 'Goibibo'");
	}
}