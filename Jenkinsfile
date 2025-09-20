pipeline {
    agent any

    environment {
        // שנה לפי שם המשתמש וה־tag שלך ב־Docker Hub
        DOCKER_IMAGE = "nofarpanker/luxe-jewelry-store:latest"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // בונה את ה-Image
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    // מחבר ל-Docker Hub
                    // החליפי USERNAME ו-PASSWORD ב-Credentials ב-Jenkins
                    withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up local Docker images..."
            sh "docker system prune -af || true"
        }
        success {
            echo "Pipeline finished successfully!"
        }
        failure {
            echo "Pipeline failed."
        }
    }
}

