pipeline {
    agent { label 'jenkins-agent' }

    environment {
        FRONT_IMAGE = 'nofar-int/luxe-jewelry-store-front:latest'
        BACK_IMAGE  = 'nofar-int/luxe-jewelry-store-backend:latest'
        AUTH_IMAGE  = 'nofar-int/luxe-jewelry-store-auth:latest'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                echo "Building Frontend Docker image..."
                dir('.') {
                    sh 'docker build -t $FRONT_IMAGE -f infra/Dockerfile.frontend .'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo "Building Backend Docker image..."
                dir('.') {
                    sh 'docker build -t $BACK_IMAGE -f infra/Dockerfile.backend .'
                }
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo "Building Auth Docker image..."
                dir('.') {
                    sh 'docker build -t $AUTH_IMAGE -f infra/Dockerfile.auth .'
                }
            }
        }

        stage('Optional: Push Docker Images') {
            steps {
                echo "Skipping push (public repo) – can add docker push here if needed."
            }
        }
    }

    post {
        always {
            echo "Pipeline finished."
        }
    }
}



