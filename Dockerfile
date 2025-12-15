FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /usr/src/app

# Install ALL browser dependencies (critical!)
RUN apt-get update && apt-get install -y \
    chromium-browser \
    chromium-chromedriver \
    firefox \
    xvfb \
    x11-xkb-utils \
    xfonts-100dpi \
    xfonts-75dpi \
    xfonts-encodings \
    xfonts-scalable \
    libxss1 \
    libgbm1 \
    libgtk-3-0 \
    libnss3 \
    libxshmfence1 \
    git \
    curl \
    wget \
    unzip \
    dbus \
    dbus-x11 \
    pulseaudio \
    && rm -rf /var/lib/apt/lists/*

# Update GeckoDriver to v0.36.0 (matches Firefox 146)
RUN wget -q https://github.com/mozilla/geckodriver/releases/download/v0.36.0/geckodriver-v0.36.0-linux64.tar.gz && \
    tar -xzf geckodriver-v0.36.0-linux64.tar.gz && \
    mv geckodriver /usr/local/bin/ && \
    chmod +x /usr/local/bin/geckodriver && \
    rm geckodriver-v0.36.0-linux64.tar.gz

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .

RUN mkdir -p src/test/resources && \
    if [ -f src/test/resources/config.properties ]; then \
        sed -i 's/execution_env=.*/execution_env=local/' src/test/resources/config.properties; \
    fi

ENV DISPLAY=:99
ENV DBUS_SYSTEM_BUS_ADDRESS=unix:path=/run/dbus/system_bus_socket

# Wait 2 seconds for Xvfb to start before running tests
CMD ["sh", "-c", "Xvfb :99 -screen 0 1920x1080x24 > /dev/null 2>&1 & sleep 2 && mvn clean test -B"]
