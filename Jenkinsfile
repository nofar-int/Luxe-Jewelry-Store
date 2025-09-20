pipeline {
    agent { label 'jenkins-agent' }
    environment {
        DOCKER_REGISTRY = "nofar-int"
        FRONTEND_IMAGE = "${DOCKER_REGISTRY}/luxe-jewelry-store-front"
        BACKEND_IMAGE  = "${DOCKER_REGISTRY}/luxe-jewelry-store-backend"
        AUTH_IMAGE     = "${DOCKER_REGISTRY}/luxe-jewelry-store-auth"
    }
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out the code..."
                checkout scm
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                echo "Building Frontend Docker image..."
                dir('jewelry-store') {
                    sh "docker build -t ${FRONTEND_IMAGE}:latest ."
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo "Building Backend Docker image..."
                dir('backend') {
                    sh "docker build -t ${BACKEND_IMAGE}:latest ."
                }
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo "Building Auth Docker image..."
                dir('auth-service') {
                    sh "docker build -t ${AUTH_IMAGE}:latest ."
                }
            }
        }

        stage('Run Tests (Optional)') {
            steps {
                echo "Running tests..."
                // לדוגמה, אם יש script test.sh בכל פרויקט:
                // sh "./test.sh"
            }
        }

        stage('Push Docker Images (Optional)') {
            steps {
                echo "Pushing images to Docker Hub..."
                // אם Docker agent מחובר עם credentials
                sh "docker push ${FRONTEND_IMAGE}:latest"
                sh "docker push ${BACKEND_IMAGE}:latest"
                sh "docker push ${AUTH_IMAGE}:latest"
            }
        }
    }

    post {
        always {
            echo "Cleaning up..."
            sh "docker images"
        }
        success {
            echo "Pipeline finished successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
