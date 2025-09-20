pipeline {
    agent { label 'jenkins-agent' }

    environment {
        FRONT_IMAGE = 'luxe-jewelry-store-front'
        BACK_IMAGE = 'luxe-jewelry-store-backend'
        AUTH_IMAGE = 'luxe-jewelry-store-auth'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the code...'
                checkout scm
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                echo 'Building Frontend Docker image...'
                sh 'docker build -t $FRONT_IMAGE ./jewelry-store'
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo 'Building Backend Docker image...'
                sh 'docker build -t $BACK_IMAGE ./backend'
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo 'Building Auth Docker image...'
                sh 'docker build -t $AUTH_IMAGE ./auth-service'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running tests...'
                // כאן אפשר להוסיף פקודות בדיקה אם יש
            }
        }

        stage('Deploy (Optional)') {
            steps {
                echo 'Deploying...'
                // כאן אפשר להוסיף פקודות לפריסה
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            sh 'docker ps -a'
            sh 'docker images'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
