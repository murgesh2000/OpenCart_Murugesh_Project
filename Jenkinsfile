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