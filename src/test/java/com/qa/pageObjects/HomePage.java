package com.qa.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends basePage {

	public HomePage(WebDriver driver) {
		super(driver);
	}


	@FindBy(className = "ico-register") WebElement lnkRegister;

	@FindBy(css = ".ico-login") WebElement lnkLogin;

	@FindBy(id = "customerCurrency") WebElement drpdownCurrency;

	@FindBy(xpath = "//div[@class='header-logo']/child::a/img")  WebElement logoHeader ;

	@FindBy(xpath = "//div[@class='topic-block-title']/h2") WebElement txtWelcome;

	public boolean islogoAvailable()
	{
		return logoHeader.isDisplayed();
	}

	public void clicklogin() {
		lnkLogin.click();
	}

	public void clickRegisterlink() {
		lnkRegister.click();
	}
}
