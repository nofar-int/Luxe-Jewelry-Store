pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                echo 'Building Frontend Docker image...'
                dir("${env.WORKSPACE}") {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-front:latest -f infra/Dockerfile.frontend .'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo 'Building Backend Docker image...'
                dir("${env.WORKSPACE}") {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-backend:latest -f infra/Dockerfile.backend .'
                }
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo 'Building Auth Docker image...'
                dir("${env.WORKSPACE}") {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-auth:latest -f infra/Dockerfile.auth .'
                }
            }
        }

        stage('Optional: Push Docker Images') {
            steps {
                echo 'Pushing Docker images...'
                sh 'docker push nofar-int/luxe-jewelry-store-front:latest'
                sh 'docker push nofar-int/luxe-jewelry-store-backend:latest'
                sh 'docker push nofar-int/luxe-jewelry-store-auth:latest'
            }
        }
    }
}



