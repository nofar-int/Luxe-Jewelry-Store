pipeline {
    agent any

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        IMAGE_NAME = 'nofarpanker/luxe-jewelry-store'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                        docker build -t \$IMAGE_NAME:\$IMAGE_TAG .
                    """
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    sh """
                        docker push \$IMAGE_NAME:\$IMAGE_TAG
                    """
                }
            }
        }

    }

    post {
        always {
            echo 'Cleaning up local Docker images...'
            sh 'docker system prune -af'
        }
    }
}

