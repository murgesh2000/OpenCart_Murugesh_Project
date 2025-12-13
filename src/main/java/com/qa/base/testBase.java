package com.qa.base;

import static org.testng.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

public class testBase {

	 //Thread-safe WebDriver
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	public static Properties p;
	public  FileReader file;
	
	
	 //Getter method (IMPORTANT)
    public static WebDriver getDriver() {
        return driver.get();
    }


	@BeforeMethod			
	@Parameters({"os","browser"})
	public void intailisation(String os, String br) throws IOException, Exception
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
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

	}

	@Test
	public void dummyTest() throws Exception
	{
		getDriver().get("https://opensource-demo.orangehrmlive.com/");
		System.out.println(getDriver().getCurrentUrl());
		assertEquals("OrangeHRM",getDriver().getTitle());
		Thread.sleep(10000);
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

	 @AfterMethod(alwaysRun = true)
	    public void tearDown() {
	        if (getDriver() != null) {
	            getDriver().quit();     
	            driver.remove();        
	        }
	    }


	@AfterSuite
	public void flush() throws Exception
	{
		Runtime.getRuntime().exec("cmd /c start stop_dockergrid.bat");
		Thread.sleep(1000);
		Runtime.getRuntime().exec("cmd /c kill_process.bat");
	}

}
