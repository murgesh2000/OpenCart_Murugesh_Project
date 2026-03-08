package com.qa.base;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class TestBase {

	// REMOVED: ThreadLocal driver and getDriver() method (Now in DriverManager)
	protected static Properties p;
	public Logger logger;

	

	private String resolveGridUrl() {
		String envUrl = System.getenv("GRID_URL");
		if (envUrl != null && !envUrl.isEmpty()) {
			return envUrl;
		}
		if (new java.io.File("/proc/1/cgroup").exists()) {
			return "http://selenium-hub:4444/wd/hub";
		}

		return "http://localhost:4444/wd/hub";
	}

	@BeforeMethod
	@Parameters({ "browser" })
	public void initialization(String br) throws IOException {
		FileReader file = new FileReader(".//src//test//resources//config.properties");
		p = new Properties();
		p.load(file);

		logger = LogManager.getLogger(this.getClass());

		String executionEnv = p.getProperty("execution_env").trim();
		WebDriver webDriver;

		if (executionEnv.equalsIgnoreCase("remote")) {
			String gridUrl = resolveGridUrl();
			System.out.println("---->"+gridUrl);
			switch (br.toLowerCase()) {
			case "chrome":
				ChromeOptions chrome = new ChromeOptions();
				chrome.setCapability("se:recordVideo", true);
				webDriver = new RemoteWebDriver(new URL(gridUrl), chrome);
				break;
			case "firefox":
				FirefoxOptions firefox = new FirefoxOptions();
				firefox.setCapability("se:recordVideo", true);
				webDriver = new RemoteWebDriver(new URL(gridUrl), firefox);
				break;
			case "edge":
				EdgeOptions edge = new EdgeOptions();
				edge.setCapability("se:recordVideo", true);
				webDriver = new RemoteWebDriver(new URL(gridUrl), edge);
				break;
			default:
				throw new RuntimeException("Unsupported Remote Browser: " + br);
			}
		} else {
			switch (br.toLowerCase()) {
			case "chrome":
				webDriver = new ChromeDriver();
				break;
			case "firefox":
				webDriver = new FirefoxDriver(new FirefoxOptions().addArguments("--headless"));
				break;
			case "edge":
				webDriver = new EdgeDriver(new EdgeOptions().addArguments("--headless=new"));
				break;
			default:
				throw new RuntimeException("Unsupported Local Browser: " + br);
			}
		}

		// UPDATED: Set the driver in DriverManager
		DriverManager.setDriver(webDriver);

		// UPDATED: Use DriverManager.getDriver() for setup
		DriverManager.getDriver().manage().window().maximize();

		int timeout = executionEnv.equalsIgnoreCase("remote") ? 60 : 20;
		DriverManager.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeout));
		DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		DriverManager.getDriver().get(p.getProperty("appURL"));
	}



	@AfterMethod
	public void afterMethod()
	{
		DriverManager.unload();
	}

}