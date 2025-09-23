pipeline {
    agent any

    environment {
        // Docker Hub credentials
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-nofarpanker')
        DOCKER_IMAGE_NAME = "nofarpanker/jenkins-agent"
    }

    stages {
        stage('Checkout') {
            steps {
                // GitHub checkout עם הקרדנשיאל הנכון
                git credentialsId: 'github-credentials-id', url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
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
                // Docker login באמצעות קרדנשיאלס המעודכן
                withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE_NAME:latest'
                }
            }
        }
    }
}

