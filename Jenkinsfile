pipeline {
    agent { label 'jenkins-agent' }

    environment {
        FRONT_IMG = "nofarpanker/luxe-frontend"
        BACK_IMG  = "nofarpanker/luxe-backend"
        AUTH_IMG  = "nofarpanker/luxe-auth"
        GIT_CREDS = "github-credentials-id"
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git',
                    branch: 'main',
                    credentialsId: "${GIT_CREDS}"
            }
        }

        stage('Build Auth Service') {
            steps {
                sh 'docker build -t $AUTH_IMG -f infra/Dockerfile.auth .'
            }
        }

        stage('Build Backend') {
            steps {
                sh 'docker build -t $BACK_IMG -f infra/Dockerfile.backend .'
            }
        }

        stage('Build Frontend') {
            steps {
                sh 'docker build -t $FRONT_IMG -f infra/Dockerfile.frontend .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withDockerRegistry([credentialsId: 'docker-hub-credentials', url: '']) {
                    sh 'docker push $AUTH_IMG'
                    sh 'docker push $BACK_IMG'
                    sh 'docker push $FRONT_IMG'
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
    }
}

