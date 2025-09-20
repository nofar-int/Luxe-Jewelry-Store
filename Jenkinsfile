pipeline {
    agent none  // ברמת ה-pipeline אין node כברירת מחדל

    stages {
        stage('Checkout') {
            agent { label 'jenkins-agent' }
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build Frontend Docker Image') {
            agent { label 'jenkins-agent' }
            steps {
                echo 'Building Frontend Docker image...'
                dir('.') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-front:latest -f infra/Dockerfile.frontend .'
                }
            }
        }

        stage('Build Backend Docker Image') {
            agent { label 'jenkins-agent' }
            steps {
                echo 'Building Backend Docker image...'
                dir('.') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-backend:latest -f infra/Dockerfile.backend .'
                }
            }
        }

        stage('Build Auth Docker Image') {
            agent { label 'jenkins-agent' }
            steps {
                echo 'Building Auth Docker image...'
                dir('.') {
                    sh 'docker build -t nofar-int/luxe-jewelry-store-auth:latest -f infra/Dockerfile.auth .'
                }
            }
        }

        stage('Optional: Push Docker Images') {
            agent { label 'jenkins-agent' }
            steps {
                echo 'Pushing images to Docker Hub...'
                sh '''
                    docker push nofar-int/luxe-jewelry-store-front:latest
                    docker push nofar-int/luxe-jewelry-store-backend:latest
                    docker push nofar-int/luxe-jewelry-store-auth:latest
                '''
            }
        }
    }
}



