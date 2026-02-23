package com.qa.TestCase;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.qa.base.TestBase;
import com.qa.pageObjects.HomePage;

public class TC_001_HomePageTest extends TestBase {
	
	@Test
	public void homePage_Test() {
		
		HomePage hp = new HomePage(getDriver());
		logger.info("Logged into the noCommerce Application");
		assertTrue(hp.islogoAvailable(), "Logo is not available");
		logger.info("noCommerce Logo is available");
		
		
		
		
	}

}
