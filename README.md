# OpenCart_Murugesh_Project

When you trigger your test execution (typically by running docker-compose up), the orchestration begins. Here is the exact order of execution and how these three files interact to run the **OpenCart\_Murugesh\_Project**.

### Phase 1: docker-compose.yml (The Orchestrator)

This is the very first file that gets executed. It acts as the master blueprint for your entire infrastructure.

1.  **Setting up the Grid:** It first reads the services and starts the selenium-hub container.
    
2.  **Attaching Nodes:** Next, it spins up the chrome, firefox, and edge browser nodes, connecting them to the hub.
    
3.  **Initializing Video Recording:** It then launches the chrome\_video, firefox\_video, and edge\_video containers, linking them to their respective browsers and mapping them to your ./Vidio\_Recordings directory.
    
4.  **Starting File Browser:** The file\_browser service is started so you can view those recordings on port 8081.
    
5.  **Triggering the Build:** Finally, it reaches the test-runner service. Instead of pulling a pre-made image, it sees the instruction build: .. This command halts the compose execution momentarily and hands control over to your Dockerfile.
    

### Phase 2: Dockerfile (The Environment Builder)

Triggered by the test-runner service, the Dockerfile executes line-by-line to build the specific environment needed to run your Java tests.

*   **Setting the Base:** It starts by pulling a clean Maven image using Eclipse Temurin Java 17, ensuring no unnecessary browsers are installed inside the runner.
    
*   **Caching Dependencies:** It copies just the pom.xml first and runs mvn dependency:go-offline -B. This is a smart caching step; it downloads all the dependencies defined in your POM before copying your actual code, saving significant time on future builds.
    
*   **Copying the Code:** It copies the rest of your hybrid framework code into the image.
    
*   **Configuring the Environment:** It executes a critical command to update your config.properties file on the fly, changing the execution environment to remote. This ensures your Java code knows to use RemoteWebDriver to talk to the Selenium Hub rather than looking for a local browser.
    
*   **Readying the Execution Command:** It sets up the final command: CMD \["mvn", "clean", "test", "-Dselenium.grid.url=${GRID\_URL}"\]. This command doesn't run during the _build_ phase. It is stored and executed only when the container is fully built and starts up. At this point, control hands over to Maven.
    

### Phase 3: pom.xml (The Test Executor)

When the test-runner container starts, it fires the mvn clean test command. Now, your pom.xml takes over.

1.  **Reading Properties:** Maven reads the properties, specifically noting that the Java compiler is set to version 11 and locating the test suite at src/test/resources/testng.xml.
    
2.  **Resolving Dependencies:** Although the Dockerfile already downloaded them, Maven verifies the dependencies. It loads Selenium (4.40.0), TestNG (7.12.0), ExtentReports, Log4j, and Apache POI for your data-driven testing.
    
3.  **Executing Tests:** The maven-surefire-plugin kicks in. It takes the testng.xml file defined in your properties and begins executing your test suites.
    
4.  **Routing Traffic:** As your Java code runs, the tests are sent out of the test-runner container to the selenium-hub (via the ${GRID\_URL} passed from Docker Compose), which distributes the tests to the browser nodes while the video containers record the sessions.



-----------------Jenkins FIle-------------------------------------------------------

This Jenkinsfile is the final piece of the puzzle. It takes the infrastructure (Docker Compose), environment (Dockerfile), and execution (POM) we just discussed and automates the entire process within a Jenkins pipeline.

Here is a breakdown of exactly what this file does, stage by stage:

### 1\. Environment Setup

*   **Agent Configuration:** It tells Jenkins that this pipeline can execute on any available worker node by using agent any.
    
*   **Environment Variables:** It globally sets the GRID\_URL variable to "http://selenium-hub:4444/wd/hub". This ensures that when the POM executes, your tests know exactly where to send the Selenium commands.
    

### 2\. Stage: Checkout

*   **Source Control:** This stage uses the checkout scm command to pull the latest version of your project's source code into the Jenkins workspace.
    

### 3\. Stage: Clean Environment

Because test environments can leave behind zombie processes or old data, this stage runs a series of Windows batch (bat) commands to guarantee a completely fresh start before running any tests:

*   **Tear Down Docker:** It spins down any lingering docker-compose services, removing volumes and orphaned containers.
    
*   **Force Remove Containers:** It explicitly forces the removal of the chrome, firefox, edge, and file\_browser containers to ensure no conflicts.
    
*   **Free Up Port 4444:** It actively searches your network statistics for any process currently listening on port 4444 (the default Selenium Hub port) and forcefully kills that specific process ID.
    
*   **Clear Old Storage:** It prunes dangling Docker containers and deletes any pre-existing .mp4 video files inside your Vidio\_Recordings directory.
    
*   **Exit Status:** It explicitly returns an exit 0 to signal to Jenkins that the cleanup sequence finished successfully.
    

### 4\. Stage: Run Automation

*   **The Execution Trigger:** It executes a batch command to run docker-compose up --build --exit-code-from test-runner.
    
*   **The Workflow:** This single line initiates the entire sequence we discussed previously: setting up the hub, launching the browser nodes, starting the video recorders, building the test runner image, and firing the Maven tests.
    
*   **Status Reporting:** The --exit-code-from test-runner flag tells Jenkins to mark the pipeline run as passed or failed based _only_ on whether your Maven tests (inside the test runner) pass or fail, ignoring the exit codes of the background browsers or video services.
    

### 5\. Post Actions

After the pipeline finishes—regardless of whether the tests passed or failed—these specific actions will always run:

*   **Archive Artifacts:** Jenkins automatically collects the newly generated .mp4 files from the Vidio\_Recordings folder and saves them as build artifacts, allowing empty archives if no videos were created.
    
*   **Final Teardown:** It runs a final docker-compose down batch command to cleanly shut down the hub, nodes, and test runner, ensuring server resources are freed up.