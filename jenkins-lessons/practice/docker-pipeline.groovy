pipeline {
    agent any

    stages {
        stage('Deploy Nginx Container') {
            steps {
                sh '''
                    docker stop nginx-cont || true
                    docker rm nginx-cont || true
                '''
                script {
                    def nginxApp = docker.image('nginx:trixie-perl')
                    nginxApp.inside {
                        sh '''
                            whoami
                            ls -lrt
                            nginx -v
                        '''
                    }
                    nginxApp.run('--name nginx-cont -dp 8081:80')
                }
            }
        }
    }
}