pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKER_USER = "nofarpanker"
        FRONT_IMG  = "luxe-frontend"
        BACK_IMG   = "luxe-backend"
        AUTH_IMG   = "luxe-auth"
    }

    stages {

        stage('Checkout SCM') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh """
                        docker build -t ${DOCKER_USER}/${AUTH_IMG}:latest -f ../infra/Dockerfile.auth .
                    """
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh """
                        docker build -t ${DOCKER_USER}/${BACK_IMG}:latest -f ../infra/Dockerfile.backend .
                    """
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh """
                        docker build -t ${DOCKER_USER}/${FRONT_IMG}:latest -f ../infra/Dockerfile.frontend .
                    """
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "docker login -u ${DOCKER_USER} && docker push ${DOCKER_USER}/${AUTH_IMG}:latest"
                sh "docker push ${DOCKER_USER}/${BACK_IMG}:latest"
                sh "docker push ${DOCKER_USER}/${FRONT_IMG}:latest"
                sh "docker logout"
            }
        }
    }

    post {
        always {
            echo "Pipeline finished"
        }
        failure {
            echo "Pipeline failed. Check logs."
        }
    }
}


