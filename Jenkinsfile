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
                // 1. Standard down
                bat 'docker-compose down -v --remove-orphans || exit 0'
                
                // 2. Clear specific containers
                bat 'docker rm -f chrome firefox edge file_browser || exit 0'
        
                // 3. IMPROVED Port Cleanup: 
                // We add "2>nul" to hide errors and ensure the block doesn't break the pipeline
                bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :4444 ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul || exit 0
                '''
        
                // 4. Global cleanup
                bat 'docker container prune -f || exit 0'
                
                // 5. Video cleanup
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