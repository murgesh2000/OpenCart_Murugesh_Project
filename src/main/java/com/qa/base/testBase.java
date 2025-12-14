package com.qa.base;

import static org.testng.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

public class testBase {

	 //Thread-safe WebDriver
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	private static Properties p;
	private static  FileReader file;
	
	
	
	 //Getter method (IMPORTANT)
    public static WebDriver getDriver() {
        return driver.get();
    }
    
    protected void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
    }

	@BeforeMethod			
	@Parameters({"browser"})
	public void intailisation(String br) throws IOException, Exception
	{
		URL gridURL = new URL("http://localhost:4444/wd/hub");
		file = new FileReader("./src//test//resources//config.properties");
		p=new Properties();
		p.load(file);
		
		WebDriver localDriver = null;
		if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
		{
			
			System.out.println("Test is Running from Docker");
			switch(br.toLowerCase())
			{
			case "chrome":
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setCapability("se:name", "LoginTest_Chrome");
				chromeOptions.setCapability("se:recordVideo", true);
				localDriver = new RemoteWebDriver(gridURL, chromeOptions);
				break;

			case "firefox":
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setCapability("se:name", "LoginTest_Firefox");
				firefoxOptions.setCapability("se:recordVideo", true);
				localDriver = new RemoteWebDriver(gridURL, firefoxOptions);
				break;

			case "edge":
				EdgeOptions edgeOptions = new EdgeOptions();
				edgeOptions.setCapability("se:name", "LoginTest_Edge");
				edgeOptions.setCapability("se:recordVideo", true);
				localDriver = new RemoteWebDriver(gridURL, edgeOptions);
				break;
			default:
                throw new RuntimeException("Invalid browser name");

			}
		}

		if(p.getProperty("execution_env").equalsIgnoreCase("local"))
		{

			switch(br.toLowerCase())
			{
			case "chrome" : localDriver=new ChromeDriver(); break;
			case "edge" : localDriver=new EdgeDriver(); break;
			case "firefox": localDriver=new FirefoxDriver(); break;
			default: throw new RuntimeException("Invalid browser name");
			}
		}
	     //Store driver in ThreadLocal
        driver.set(localDriver);
		getDriver().manage().window().maximize();
		getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
	}

	@Test
	public void dummyTest() throws Exception
	{
		getDriver().get("https://opensource-demo.orangehrmlive.com/");
		System.out.println(getDriver().getCurrentUrl());
		assertEquals("OrangeHRM",getDriver().getTitle());
		getDriver().findElement(By.name("username")).sendKeys("admin");
		getDriver().findElement(By.name("password")).sendKeys("admin123");
		WebDriverWait wait = new WebDriverWait(getDriver(),Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Login']")));
		getDriver().findElement(By.xpath("//button[normalize-space()='Login']")).click();		
	}

	@BeforeSuite
	public void beforeSuit() throws Exception
	{
		try {
			Runtime.getRuntime().exec("cmd /c start start_dockergrid.bat");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		Thread.sleep(10000);
	}

	 @AfterMethod()
	    public void tearDown() {
	        if (getDriver() != null) {
	            getDriver().quit();     
	            driver.remove();        
	        }
	    }


//	@AfterSuite
	public void flush() throws Exception
	{
		Runtime.getRuntime().exec("cmd /c start stop_dockergrid.bat");
		Thread.sleep(1000);
		Runtime.getRuntime().exec("cmd /c kill_process.bat");
	}

}
