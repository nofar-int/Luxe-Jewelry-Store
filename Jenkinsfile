pipeline {
    agent any
    environment {
        PATH = "/usr/bin:$PATH" // Docker נמצא כאן
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: 'main']],
                  userRemoteConfigs: [[url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git']]])
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh 'docker build -t nofarpanker/luxe-auth:latest -f ../Dockerfile.auth .'
                }
            }
        }

        stage('Build Backend Service') {
            steps {
                dir('backend-service') {
                    sh 'docker build -t nofarpanker/luxe-backend:latest -f ../Dockerfile.backend .'
                }
            }
        }

        stage('Build Frontend Service') {
            steps {
                dir('frontend') {
                    sh 'docker build -t nofarpanker/luxe-frontend:latest -f ../Dockerfile.frontend .'
                }
            }
        }

        stage('Build Agent Service') {
            steps {
                sh 'docker build -t nofarpanker/luxe-agent:latest -f Dockerfile.agent .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh '''
                    docker login -u <your_dockerhub_username>
                    docker push nofarpanker/luxe-auth:latest
                    docker push nofarpanker/luxe-backend:latest
                    docker push nofarpanker/luxe-frontend:latest
                    docker push nofarpanker/luxe-agent:latest
                    docker logout
                '''
            }
        }
    }
}


