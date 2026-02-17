pipeline {
    agent any

    environment {
        // Ensure the Grid URL points to the internal Docker service name
        GRID_URL = "http://selenium-hub:4444/wd/hub"
    }

    stages {
        stage('Checkout') {
            steps {
                // Pulls code from the Git repo configured in the Jenkins Job
                checkout scm
            }
        }

        stage('Clean Environment') {
            steps {
                sh 'docker-compose down --remove-orphans'
                sh 'rm -rf Vidio_Recordings/*.mp4'
            }
        }

        stage('Run Automation') {
            steps {
                // The --exit-code-from flag is critical for Jenkins to know if tests passed/failed
                sh 'docker-compose up --build --exit-code-from test-runner'
            }
        }
    }

    post {
        always {
            // Archive the videos so you can watch them in the Jenkins UI
            archiveArtifacts artifacts: 'Vidio_Recordings/*.mp4', allowEmptyArchive: true
            
            // Shut down the grid to free up resources
            sh 'docker-compose down'
        }
    }
}