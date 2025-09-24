pipeline {
    agent { label 'jenkins-agent' }

    environment {
        // שם המשתמש בדוקר־האב
        DOCKER_HUB_USER = 'nofarpanker'

        // שמות האימג'ים
        FRONT_IMG = "luxe-frontend"
        BACK_IMG  = "luxe-backend"
        AUTH_IMG  = "luxe-auth"
    }

    stages {
        stage('Checkout') {
            steps {
                // ***** שימוש ב-HTTPS ובקרדשנלס *****
                git branch: 'main',
                    url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git',
                    credentialsId: 'github-credentials-id'
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh "docker build -t ${DOCKER_HUB_USER}/${AUTH_IMG}:latest -f ../infra/Dockerfile.auth ."
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh "docker build -t ${DOCKER_HUB_USER}/${BACK_IMG}:latest -f ../infra/Dockerfile.backend ."
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh "docker build -t ${DOCKER_HUB_USER}/${FRONT_IMG}:latest -f ../infra/Dockerfile.frontend ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials-id',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_HUB_USER}/${AUTH_IMG}:latest
                        docker push ${DOCKER_HUB_USER}/${BACK_IMG}:latest
                        docker push ${DOCKER_HUB_USER}/${FRONT_IMG}:latest
                        docker logout
                    '''
                }
            }
        }
    }
}
