pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start Grid + Run Tests') {
            steps {
                // Build images and start only test-runner + its dependencies
                bat 'docker compose -f docker-compose.yaml up --build --abort-on-container-exit test-runner'
            }
        }
    }

    post {
        always {
            // Stop and remove all containers/networks/volumes defined in compose
            bat 'docker compose -f docker-compose.yaml down || true'

            // Archive video recordings folder if present
            archiveArtifacts artifacts: 'Vidio_Recordings/**/*.*', allowEmptyArchive: true
        }
    }
}
