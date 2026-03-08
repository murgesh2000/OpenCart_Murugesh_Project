package com.qa.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.qa.base.DriverManager;

public class basePage {

	WebDriver driver;

	public basePage(WebDriver driver) {

		this.driver = driver;
		PageFactory.initElements(DriverManager.getDriver(), this);


	}
}
