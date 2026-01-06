pipeline {
    agent {
        docker {
            image 'node:trixie-slim'
            args '-u root'
        }
    }

    stages {
        stage('Check Command inside docker agent') {
            steps {
                sh '''
                    node --version
                    pwd
                    ls -lrt
                '''
            }
        }
    }
}