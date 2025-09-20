pipeline {
    agent { label 'jenkins-agent' }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                dir('infra') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-front:latest -f Dockerfile.frontend .'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                dir('infra') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-backend:latest -f Dockerfile.backend .'
                }
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                dir('infra') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-auth:latest -f Dockerfile.auth .'
                }
            }
        }

        stage('Optional: Push Docker Images') {
            steps {
                echo "Optional: Push images to Docker Hub or registry"
            }
        }
    }
}


