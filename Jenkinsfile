pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials-id') // החליפי ב-ID של קרדנציאל הדוקר
        DOCKER_IMAGE_NAME = "nofarpanker/jenkins-agent" // או כל אימג' אחר שלך
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-credentials-id', url: 'https://github.com/nofarpanker/your-repo.git'
            }
        }

        stage('Build Frontend') {
            steps {
                sh 'docker build -t luxe-jewelry-store-front:latest -f Dockerfile .'
            }
        }

        stage('Build Agent Image') {
            steps {
                sh 'docker build -t jenkins-agent:latest -f Dockerfile.agent .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials-id', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE_NAME:latest'
                }
            }
        }
    }
}
