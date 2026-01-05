pipeline {
    agent any

    tools {
        nodejs 'nodejs-lts'
    }

    stages {
        stage('Clone Code') {
            steps {
                git 'https://github.com/PisethMao/reactjs-template-product'
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

        stage('Build') {
            steps {
                sh '''
                    docker build -t pisethmao/jenkins-react-pipeline:${env.BUILD_NUMBER} .
                '''
            }
        }

        stage('Push to Dockerhub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                    // some block
                    sh '''
                        echo "$PASSWORD" | docker login -u "$USERNAME" --password-stdin
                        docker push pisethmao/jenkins-react-pipeline:${env.BUILD_NUMBER}
                    ''' 
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop reactjs-cont || true
                    docker rm -f reactjs-cont || true
                    docker run -d -p 3000:8080 --name reactjs-cont jenkins-react-pipeline
                '''
            }
        }

        stage('Add Domain Name') {
            steps {
                sh '''
                    echo "Running shellscript to add the domain name for the service."
                '''
            }
        }
    }
}