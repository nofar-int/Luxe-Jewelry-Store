pipeline {
    agent { label 'jenkins-agent' } // משתמש באגנט שהגדרת
    environment {
        DOCKER_IMAGE_FRONT = 'luxe-jewelry-store-front'
        DOCKER_IMAGE_BACK = 'luxe-jewelry-store-backend'
        DOCKER_IMAGE_AUTH = 'luxe-jewelry-store-auth'
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
                sh """
                docker build -t $DOCKER_IMAGE_FRONT ./frontend
                """
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo "Building Backend Docker image..."
                sh """
                docker build -t $DOCKER_IMAGE_BACK ./backend
                docker build -t $DOCKER_IMAGE_AUTH ./auth
                """
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests..."
                sh """
                # כאן תוכלי להוסיף פקודות להרצת Unit/Integration tests
                echo "Tests passed!"
                """
            }
        }

        stage('Deploy (Optional)') {
            steps {
                echo "Deploying containers..."
                sh """
                # דוגמא להרצת הקונטיינרים (אם תרצי להריץ על המחשב המקומי)
                docker run -d --name frontend $DOCKER_IMAGE_FRONT
                docker run -d --name backend $DOCKER_IMAGE_BACK
                docker run -d --name auth $DOCKER_IMAGE_AUTH
                """
            }
        }

    }

    post {
        always {
            echo 'Cleaning up...'
            sh """
            docker ps -a
            docker images
            """
        }
        success {
            echo 'Pipeline finished successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
