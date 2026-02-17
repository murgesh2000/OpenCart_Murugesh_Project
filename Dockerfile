# 1. Use a clean Maven image (No browsers needed!)
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /usr/src/app

# 2. Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 3. Copy the rest of your hybrid framework code
COPY . .

# 4. Critical: Ensure your config.properties points to 'remote' 
# so your Java code uses RemoteWebDriver to talk to the Hub.
RUN sed -i 's/execution_env=.*/execution_env=remote/' src/test/resources/config.properties

# 5. Run Maven. The GRID_URL is passed from docker-compose.yml
# No Xvfb or display needed here; the nodes handle the UI.
CMD ["mvn", "clean", "test", "-Dselenium.grid.url=${GRID_URL}"]