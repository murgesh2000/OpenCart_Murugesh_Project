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
                // Change 'sh' to 'bat' for Windows
                bat 'docker-compose down --remove-orphans'
                // 2. Extra safety: Specifically remove the file_browser if it's still stuck
                bat 'docker rm -f file_browser || exit 0'
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