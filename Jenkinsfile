pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Build Auth Service') {
            steps {
                sh 'docker build -t nofarpanker/luxe-auth:latest -f Dockerfile.auth .'
            }
        }

        stage('Build Backend Service') {
            steps {
                sh 'docker build -t nofarpanker/luxe-backend:latest -f Dockerfile.backend .'
            }
        }

        stage('Build Frontend Service') {
            steps {
                sh 'docker build -t nofarpanker/luxe-frontend:latest -f Dockerfile.frontend .'
            }
        }

        stage('Build Agent Service') {
            steps {
                sh 'docker build -t nofarpanker/luxe-agent:latest -f Dockerfile.agent .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh 'docker login -u nofarpanker' // ציבורי, אין סיסמה
                sh 'docker push nofarpanker/luxe-auth:latest'
                sh 'docker push nofarpanker/luxe-backend:latest'
                sh 'docker push nofarpanker/luxe-frontend:latest'
                sh 'docker push nofarpanker/luxe-agent:latest'
                sh 'docker logout'
            }
        }
    }
}

