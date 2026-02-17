package com.qa.base;

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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class TestBase {

	// Thread-safe WebDriver for parallel execution
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	protected static Properties p;

	public static WebDriver getDriver() {
		return driver.get();
	}

	/**
	 * Determines the Grid URL. 
	 * Priority: Environment Variable > Docker Internal DNS > Localhost
	 */
	private String resolveGridUrl() {
		String envUrl = System.getenv("GRID_URL");
		if (envUrl != null && !envUrl.isEmpty()) {
			return envUrl;
		}

		// Check if running inside a container
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

		String executionEnv = p.getProperty("execution_env").trim();
		WebDriver webDriver;

		System.out.println("Execution Mode: " + executionEnv.toUpperCase());
		System.out.println("Target Browser: " + br);

		if (executionEnv.equalsIgnoreCase("remote")) {
			String gridUrl = resolveGridUrl();
			System.out.println("Connecting to Selenium Grid: " + gridUrl);

			switch (br.toLowerCase()) {
			case "chrome":
				ChromeOptions chrome = new ChromeOptions();
				// Enable video recording capability for the 'selenium/video' containers
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
			// LOCAL EXECUTION (Uses local drivers like chromedriver.exe)
			switch (br.toLowerCase()) {
			case "chrome":
				webDriver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
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

		driver.set(webDriver);
		getDriver().manage().window().maximize();

		int timeout = executionEnv.equalsIgnoreCase("remote") ? 60 : 20;
		getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeout));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		if (getDriver() != null) {
			getDriver().quit();
			driver.remove();
		}
	}
}