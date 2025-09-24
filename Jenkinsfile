pipeline {
    agent { label 'jenkins-agent' }
    environment {
        GIT_CREDS = credentials('github-credentials-id')
    }
    stages {
        stage('Build Backend') {
            steps {
                dir('backend-service') {
                    sh 'docker build -t luxe-jewelry-store-backend .'
                }
            }
        }
        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh 'docker build -t luxe-jewelry-store-auth .'
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh 'docker build -t luxe-jewelry-store-front -f Dockerfile.java .'
                }
            }
        }
        stage('Run All Services') {
            steps {
                script {
                    sh '''
                    docker run -d -p 8000:8000 --name backend-service luxe-jewelry-store-backend || echo "Backend already running"
                    docker run -d -p 8001:8001 --name auth-service luxe-jewelry-store-auth || echo "Auth already running"
                    docker run -d -p 3000:80 --name frontend luxe-jewelry-store-front || echo "Frontend already running"
                    '''
                }
            }
        }
    }
}
