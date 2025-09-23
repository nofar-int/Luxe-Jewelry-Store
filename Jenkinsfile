pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-nofarpanker') // ID נכון של קרדנציאל הדוקר
        DOCKER_IMAGE_NAME = "nofarpanker/jenkins-agent" // שם האימג' שלך
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-nofarpanker', url: 'https://github.com/nofarpanker/Luxe-Jewelry-Store.git'
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
                withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE_NAME:latest'
                }
            }
        }
    }
}

