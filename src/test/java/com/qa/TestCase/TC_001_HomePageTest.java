package com.qa.TestCase;

import static org.testng.Assert.assertTrue;

import java.time.Duration;

import org.testng.annotations.Test;

import com.qa.base.DriverManager;
import com.qa.base.TestBase;
import com.qa.pageObjects.HomePage;

public class TC_001_HomePageTest extends TestBase {
	
	@Test
	public void homePage_Test() throws InterruptedException {
		
		HomePage hp = new HomePage(DriverManager.getDriver());
		logger.info("Logged into the noCommerce Application");
		assertTrue(hp.islogoAvailable(), "Logo is not available");
		logger.info("noCommerce Logo is available");
		
		
		
	}

}