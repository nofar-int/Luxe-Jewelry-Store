pipeline {
    agent { label 'jenkins-agent' }
    
    environment {
        FRONT_IMAGE = 'nofar-int/luxe-jewelry-store-front:latest'
        BACK_IMAGE  = 'nofar-int/luxe-jewelry-store-backend:latest'
        AUTH_IMAGE  = 'nofar-int/luxe-jewelry-store-auth:latest'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the code...'
                checkout scm
            }
        }
        
        stage('Build Frontend Docker Image') {
            steps {
                echo 'Building Frontend Docker image...'
                dir('infra') {
                    sh "docker build -t ${env.FRONT_IMAGE} -f Dockerfile.frontend ."
                }
            }
        }
        
        stage('Build Backend Docker Image') {
            steps {
                echo 'Building Backend Docker image...'
                dir('infra') {
                    sh "docker build -t ${env.BACK_IMAGE} -f Dockerfile.backend ."
                }
            }
        }
        
        stage('Build Auth Docker Image') {
            steps {
                echo 'Building Auth Docker image...'
                dir('infra') {
                    sh "docker build -t ${env.AUTH_IMAGE} -f Dockerfile.auth ."
                }
            }
        }
        
        stage('Optional: Push Docker Images') {
            steps {
                echo 'Pushing Docker images (optional)...'
                sh "docker push ${env.FRONT_IMAGE} || true"
                sh "docker push ${env.BACK_IMAGE} || true"
                sh "docker push ${env.AUTH_IMAGE} || true"
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finished.'
            sh 'docker images'
        }
    }
}

