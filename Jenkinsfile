pipeline {
    agent any

    environment {
        GRID_URL = "http://selenium-hub:4444/wd/hub"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean Environment') {
            steps {
                // 1. Forcefully stop and remove EVERYTHING related to this compose file
                bat 'docker-compose down -v --remove-orphans'
                
               // Target specific named containers that are causing conflicts
                 // The '|| exit 0' ensures the pipeline continues even if the container isn't there
                bat 'docker rm -f chrome firefox edge file_browser || exit 0'
                
                // 3. NEW: Kill any process using port 4444 (The Hub port)
                // This finds the Process ID (PID) using 4444 and terminates it
                bat 'for /f "tokens=5" %%a in (\'netstat -ano ^| findstr :4444 ^| findstr LISTENING\') do taskkill /f /pid %%a || exit 0'
        
                 // 2. Wipe out ANY container in the system that is not currently running
                 // This clears 'firefox', 'chrome', etc., if they are stuck
                bat 'docker container prune -f'
        
                // 3. Clean up old videos
                bat 'if exist Vidio_Recordings\\*.mp4 del /q Vidio_Recordings\\*.mp4'
            }
        }

        stage('Run Automation') {
            steps {
                // Change 'sh' to 'bat'
                bat 'docker-compose up --build --exit-code-from test-runner'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'Vidio_Recordings/*.mp4', allowEmptyArchive: true
            bat 'docker-compose down'
        }
    }
}