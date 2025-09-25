pipeline {
    agent { label 'jenkins-agent' }
    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
    }
    stages {
        stage('Checkout') {
            steps {
                // וודא שהוא בודק את ה-branch main
                git branch: 'main', url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
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
        stage('Push to Docker Hub') {
            steps {
                sh "echo $DOCKER_HUB_CRED_PSW | docker login -u $DOCKER_HUB_CRED_USR --password-stdin"
                sh 'docker push nofarpanker/luxe-auth:latest'
                sh 'docker push nofarpanker/luxe-backend:latest'
                sh 'docker push nofarpanker/luxe-frontend:latest'
            }
        }
    }
}


