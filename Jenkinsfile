pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKER_REGISTRY = "nofar-int"
        FRONT_IMAGE = "${DOCKER_REGISTRY}/luxe-jewelry-store-front:latest"
        BACK_IMAGE = "${DOCKER_REGISTRY}/luxe-jewelry-store-backend:latest"
        AUTH_IMAGE = "${DOCKER_REGISTRY}/luxe-jewelry-store-auth:latest"
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
                dir('jewelry-store') {
                    sh 'docker build -t $FRONT_IMAGE .'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo 'Building Backend Docker image...'
                dir('backend') {
                    sh 'docker build -t $BACK_IMAGE .'
                }
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo 'Building Auth Docker image...'
                dir('auth-service') {
                    sh 'docker build -t $AUTH_IMAGE .'
                }
            }
        }

        stage('Run Tests (Optional)') {
            steps {
                echo 'Running tests...'
                // כאן אפשר להריץ את בדיקות האפליקציה
            }
        }

        stage('Push Docker Images (Optional)') {
            steps {
                echo 'Pushing Docker images to registry...'
                sh 'docker push $FRONT_IMAGE'
                sh 'docker push $BACK_IMAGE'
                sh 'docker push $AUTH_IMAGE'
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            sh 'docker images'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

