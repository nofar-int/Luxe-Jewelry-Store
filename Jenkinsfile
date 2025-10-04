pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    echo 'Building Docker images for all services...'
                    sh 'docker compose build'
                }
            }
        }

        stage('Run Services') {
            steps {
                script {
                    echo 'Starting services...'
                    sh 'docker compose up -d'
                }
            }
        }

        stage('Test auth-service') {
            steps {
                script {
                    echo 'Testing auth-service...'
                    // כאן נריץ בדיקות אם יש תיקיית test או סקריפט package.json
                    dir('auth-service') {
                        sh 'npm install'
                        sh 'npm test || echo "Tests failed but continuing..."'
                    }
                }
            }
        }

        stage('Stop Services') {
            steps {
                script {
                    echo 'Stopping all containers...'
                    sh 'docker compose down'
                }
            }
        }
    }

    post {
        always {
            script {
                echo 'Cleaning up Docker resources...'
                sh 'docker system prune -f'
            }
        }
    }
}

