pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-nofarpanker') // קרדנציאל דוקר בלבד
        DOCKER_IMAGE_NAME = "nofarpanker/luxe-jewelry-store-agent"  // שם האימג' בדוקר
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Build Frontend') {
            steps {
                sh 'docker build -t luxe-jewelry-store-front:latest -f Dockerfile .'
            }
        }

        stage('Build Agent Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE_NAME:latest -f Dockerfile.agent .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE_NAME:latest'
                }
            }
        }
    }
}

