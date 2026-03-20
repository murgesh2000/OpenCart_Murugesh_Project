package com.qa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporterListeners implements ITestListener {
	public static ExtentReports extent;
	public static ExtentSparkReporter spark;
	String repName;

	// The "Magic" ThreadLocal variable
	public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	@Override
	public void onStart(ITestContext context) {
		if (extent == null) {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			repName = "Test-Report-" + timeStamp + ".html";
			spark = new ExtentSparkReporter(".\\reports\\" + repName);

			spark.config().setDocumentTitle("OpenCart Automation Report");
			spark.config().setReportName("Regression Testing");
			spark.config().setTheme(Theme.STANDARD);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			extent.setSystemInfo("Application", "OpenCart");
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
		}
	}


	@Override
	public void onTestStart(ITestResult result) {
		// Create the test entry in the report and store it in ThreadLocal
		ExtentTest t = extent.createTest(result.getMethod().getMethodName());
		t.assignCategory(result.getMethod().getGroups());
		test.set(t); 
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.get().log(Status.PASS, result.getName() + " passed successfully.");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test.get().log(Status.FAIL, result.getName() + " failed.");
		test.get().log(Status.INFO, result.getThrowable().getMessage());
		// You can add screenshot logic here: test.get().addScreenCaptureFromPath(path);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		test.get().log(Status.SKIP, result.getName() + " skipped.");
	}

	@Override
	public void onFinish(ITestContext context) {
		if (context.getSuite().getAllMethods().size() > 0) {
			extent.flush();
		}
		test.remove(); // Important: Prevents memory leaks
		//		String pathOfExtentReport = System.getProperty("user.dir")+"\\reports\\"+repName;
		//		File extentReport = new File(pathOfExtentReport);
		//
		//		
		//	
		//		try {
		//			Desktop.getDesktop().browse(extentReport.toURI());
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}

	}
}