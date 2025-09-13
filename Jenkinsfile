pipeline {
    agent any

    environment {
        DOCKER_USER = credentials('docker-hub-username')
        DOCKER_PASS = credentials('docker-hub-password')
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t luxe-jewelry-store-auth ./auth-service'
                    sh 'docker build -t luxe-jewelry-store-backend ./backend-service'
                    sh 'docker build -t luxe-jewelry-store-front ./front'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh 'docker tag luxe-jewelry-store-auth nofarpanker/luxe-jewelry-store-auth:latest'
                    sh 'docker tag luxe-jewelry-store-backend nofarpanker/luxe-jewelry-store-backend:latest'
                    sh 'docker tag luxe-jewelry-store-front nofarpanker/luxe-jewelry-store-front:latest'
                    sh 'docker push nofarpanker/luxe-jewelry-store-auth:latest'
                    sh 'docker push nofarpanker/luxe-jewelry-store-backend:latest'
                    sh 'docker push nofarpanker/luxe-jewelry-store-front:latest'
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker system prune -f'
            }
        }
    }
}
