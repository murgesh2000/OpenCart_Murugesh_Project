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

    // Thread-safe WebDriver
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    protected static Properties p;

    public static WebDriver getDriver() {
        return driver.get();
    }

    /* ================= GRID URL AUTO SWITCH ================= */

    private String resolveGridUrl() {
        // 1️⃣ Explicit env (CI / Docker override)
        String envUrl = System.getenv("GRID_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            return envUrl;
        }

        // 2️⃣ Inside Docker
        if (isRunningInsideDocker()) {
            return "http://selenium-hub:4444/wd/hub";
        }

        // 3️⃣ Local fallback (Windows / Eclipse)
        return "http://localhost:4444/wd/hub";
    }

    private boolean isRunningInsideDocker() {
        return new java.io.File("/proc/1/cgroup").exists();
    }

    /* ================= INITIALIZATION ================= */

    @BeforeMethod
    @Parameters({ "browser" })
    public void initialization(String br) throws IOException {
        // Load config safely (works in Docker + Windows)
        FileReader file = new FileReader(".//src//test//resources//config.properties");
        p = new Properties();
        p.load(file);

        String executionEnv = p.getProperty("execution_env").trim();

        System.out.println("Execution Env : " + executionEnv);
        System.out.println("Browser : " + br);

        WebDriver webDriver;

        // ✅ FIX: Check execution_env FIRST, then print grid URL only if needed
        if (executionEnv.equalsIgnoreCase("remote")) {
            String gridUrl = resolveGridUrl();
            System.out.println("Using Grid URL: " + gridUrl);

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
                    throw new RuntimeException("Invalid browser: " + br);
            }

        } else { // LOCAL (including Docker headless)
            System.out.println("Using LOCAL browser: " + br);

            switch (br.toLowerCase()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    // Essential for Docker headless execution
                    chromeOptions.addArguments("--headless=new");          // ✅ Headless mode
                    chromeOptions.addArguments("--no-sandbox");            // ✅ Required for Docker
                    chromeOptions.addArguments("--disable-dev-shm-usage"); // ✅ Reduce memory usage
                    chromeOptions.addArguments("--disable-gpu");           // ✅ Disable GPU
                    chromeOptions.addArguments("--window-size=1920,1080"); // Set window size
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--disable-default-apps");
                    webDriver = new ChromeDriver(chromeOptions);
                    break;

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    // Essential for Docker headless execution
                    firefoxOptions.addArguments("--headless");             // ✅ Headless mode
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    webDriver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    // Essential for Docker headless execution
                    edgeOptions.addArguments("--headless=new");            // ✅ Headless mode
                    edgeOptions.addArguments("--no-sandbox");              // ✅ Required for Docker
                    edgeOptions.addArguments("--disable-dev-shm-usage");   // ✅ Reduce memory usage
                    edgeOptions.addArguments("--disable-gpu");             // ✅ Disable GPU
                    edgeOptions.addArguments("--window-size=1920,1080");   // Set window size
                    edgeOptions.addArguments("--disable-notifications");
                    webDriver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    throw new RuntimeException("Invalid browser: " + br);
            }
        }

        // ✅ Correct ThreadLocal assignment
        driver.set(webDriver);
        getDriver().manage().window().maximize();

        // Set appropriate timeout based on execution environment
        if (executionEnv.equalsIgnoreCase("remote")) {
            // Remote: 60 seconds for page load (Docker network may be slower)
            getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        } else {
            // Local: 20 seconds for page load
            getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        }
    }

    /* ================= TEARDOWN ================= */

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}