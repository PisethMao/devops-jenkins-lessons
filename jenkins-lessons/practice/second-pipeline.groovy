pipeline {
    agent any

    environment {
        REPO_NAME = 'pisethmao'
        IMAGE_NAME = 'jenkins-reactjs'
        TAG = 'latest'
    }

    tools {
        nodejs 'nodejs-lts'
    }

    stages {
        stage('Clone Code') {
            steps {
                git 'https://github.com/sexymanalive/reactjs-devop10-template'
            }
        }

        stage('Run Test') {
            steps {
                sh '''
                    npm install --force
                    npm test
                '''
            }
        }

        stage('Build Image') {
            steps {
                sh '''
                    docker build -t "${REPO_NAME}/${IMAGE_NAME}:$TAG" .
                '''
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB-CREDENTIALS', passwordVariable: 'DH_PASSWORD', usernameVariable: 'DH_USERNAME')]) {
                    // some block
                    sh '''
                        echo "$DH_PASSWORD" | docker login -u "$DH_USERNAME" --password-stdin
                        docker push "${REPO_NAME}/${IMAGE_NAME}:$TAG"
                    '''
                }
            }
        }

        stage('Run Service') {
            steps {
                sh '''
                    docker stop react-app || true
                    docker rm -f react-app || true
                    docker run -dp 3005:8080 --name reactjs-cont-5 "${REPO_NAME}/${IMAGE_NAME}:$TAG"
                '''
            }
        }
    }
}