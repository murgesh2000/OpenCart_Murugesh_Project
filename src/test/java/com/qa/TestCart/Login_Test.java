package com.qa.TestCart;

import org.testng.annotations.Test;

import com.qa.base.TestBase;

public class Login_Test extends TestBase {

	@Test
	public void loginTest() {
		getDriver().get("https://parabank.parasoft.com/parabank/index.htm");
		System.out.println(getDriver().getCurrentUrl());

		////		 Additional test steps can be added here
		//		 assertEquals("OrangeHRM", getDriver().getTitle());
		//		 getDriver().findElement(By.name("username")).sendKeys("admin");
		//		 getDriver().findElement(By.name("password")).sendKeys("admin123");
		//		 WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
		//		 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Login']")));
		//		 getDriver().findElement(By.xpath("//button[normalize-space()='Login']")).click();
	}
}
