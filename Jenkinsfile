pipeline {
    agent { label 'docker-agent' }
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-nofarpanker')
    }
    stages {
        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh 'docker build -t luxe-jewelry-store-front .'
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'docker build -t luxe-jewelry-store-backend .'
                }
            }
        }
        stage('Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'echo $PASSWORD | docker login -u $USERNAME --password-stdin'
                    sh 'docker push luxe-jewelry-store-front'
                    sh 'docker push luxe-jewelry-store-backend'
                }
            }
        }
    }
}


