package com.qa.utils;

import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.Status;
import com.qa.base.TestBase;

public class GenericFunctions extends TestBase {

	public void clickElement(WebElement element, String elementName) {
		try {
			element.click();
			// This pulls the CORRECT test instance for the current thread
			ReporterListeners.test.get().log(Status.PASS, "Clicked on: " + elementName);
		} catch (Exception e) {
			ReporterListeners.test.get().log(Status.FAIL, "Failed to click on " + elementName + ": " + e.getMessage());
			throw e;
		}
	}

}
