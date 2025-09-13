pipeline {
    agent any

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKER_IMAGE_AUTH = "nofarpanker/luxe-jewelry-store-auth"
        DOCKER_IMAGE_BACKEND = "nofarpanker/luxe-jewelry-store-backend"
        DOCKER_IMAGE_FRONT = "nofarpanker/luxe-jewelry-store-front"
    }

    stages {

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin

                        # Build and push auth image
                        docker build -t $DOCKER_IMAGE_AUTH:latest auth/
                        docker push $DOCKER_IMAGE_AUTH:latest

                        # Build and push backend image
                        docker build -t $DOCKER_IMAGE_BACKEND:latest backend/
                        docker push $DOCKER_IMAGE_BACKEND:latest

                        # Build and push front image
                        docker build -t $DOCKER_IMAGE_FRONT:latest front/
                        docker push $DOCKER_IMAGE_FRONT:latest
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up local Docker images..."
            sh '''
                docker rmi $DOCKER_IMAGE_AUTH:latest || true
                docker rmi $DOCKER_IMAGE_BACKEND:latest || true
                docker rmi $DOCKER_IMAGE_FRONT:latest || true
            '''
        }
    }
}
