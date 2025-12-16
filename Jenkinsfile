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
        bat '''
            docker compose -f docker-compose.yaml down
            if %ERRORLEVEL% NEQ 0 (
              echo docker compose down failed, ignoring
            )
        '''
        archiveArtifacts artifacts: 'Vidio_Recordings/**/*.*', allowEmptyArchive: true
    }
}

}
