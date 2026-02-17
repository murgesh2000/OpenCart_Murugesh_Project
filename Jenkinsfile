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
                bat """
                    @echo off
                    echo --- Cleaning Docker Resources ---
                    docker-compose down -v --remove-orphans 2>nul
                    docker rm -f chrome firefox edge file_browser 2>nul
                    
                    echo --- Checking Port 4444 ---
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :4444 ^| findstr LISTENING') do (
                        echo Killing process %%a using port 4444
                        taskkill /f /pid %%a 2>nul
                    )
        
                    echo --- Cleaning up Volumes and Videos ---
                    docker container prune -f 2>nul
                    if exist Vidio_Recordings\\*.mp4 del /q Vidio_Recordings\\*.mp4
                    
                    echo --- Cleanup Finished Successfully ---
                    exit 0
                """
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