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
                // Ignore errors on 'down' if docker is momentarily unreachable
                bat 'docker-compose down -v --remove-orphans || exit 0'
                
                bat 'docker rm -f chrome firefox edge file_browser || exit 0'

                // Port cleanup (already proven to work!)
                bat 'for /f "tokens=5" %%a in (\'netstat -ano ^| findstr :4444 ^| findstr LISTENING\') do taskkill /f /pid %%a || exit 0'

                // Add '|| exit 0' here so the build doesn't die if the API is flickering
                bat 'docker container prune -f || exit 0'
                
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