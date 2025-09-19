pipeline {
    agent {
        docker {
            image 'nofarpanker/jenkins-docker-agent:latest'
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKERHUB_CREDENTIALS = 'docker-hub-creds'
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
                sh """
                    docker login --username \$DOCKERHUB_USERNAME --password \$DOCKERHUB_PASSWORD
                    docker build -t \$IMAGE_NAME:\$IMAGE_TAG .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh """
                        docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD
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
