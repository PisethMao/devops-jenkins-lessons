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

        stage('Check Code Quality in SonarQube') {
            environment {
                scannerHome = tool 'sonarqube-scanner'
            }

            steps {
                withSonarQubeEnv(credentialsId: 'SONARQUBE_TOKEN', installationName: 'sonarqube-scanner') {
                    script {
                        def projectKey = 'reactjs-template-product'
                        def projectName = 'ReactjsTemplateProduct'
                        def projectVersion = '1.0.0'
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${projectKey} \
                            -Dsonar.projectName=${projectName} \
                            -Dsonar.projectVersion=${projectVersion}
                        """
                    }
                }
            }
        }

        stage('Wait for Quality Gate') {
            steps {
                script {
                    def qg = waitForQualityGate(abortPipeline: true)
                    if (qg.status != 'OK') {
                        currentBuild.result = 'FAILURE'
                        echo "Quality gate failed: ${qg.status}. Stopping pipeline"
                        return
                    }
                    echo "Quality gate passed!"
                    currentBuild.result == 'SUCCESS'
                }
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
            // when {
            //     expression {
            //         currentBuild.result == 'SUCCESS'
            //     }
            // }
            steps {
                echo 'Building the docker image'
                sh '''
                    docker build -t "${REPO_NAME}/${IMAGE_NAME}:$TAG" .
                '''
            }
        }

        stage('Push Image') {
            // when {
            //     expression {
            //         currentBuild.result == 'SUCCESS'
            //     }
            // }
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB-CREDENTIALS', passwordVariable: 'DH_PASSWORD', usernameVariable: 'DH_USERNAME')]) {
                    // some block
                    echo 'Pushing the docker image to registry'
                    sh '''
                        echo "$DH_PASSWORD" | docker login -u "$DH_USERNAME" --password-stdin
                        docker push "${REPO_NAME}/${IMAGE_NAME}:$TAG"
                    '''
                }
            }
        }

        stage('Run Service') {
            // when {
            //     expression {
            //         currentBuild.result == 'SUCCESS'
            //     }
            // }
            steps {
                sh '''
                    docker stop react-app || true
                    docker rm -f react-app || true
                    docker run -dp 3010:8080 --name reactjs-cont-10 "${REPO_NAME}/${IMAGE_NAME}:$TAG"
                '''
            }
        }
    }
}