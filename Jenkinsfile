pipeline {
    agent { label 'jenkins-agent' }   // שם הלייבל של האג’נט המרוחק

    environment {
        REGISTRY      = "nofarpanker"        // שם המשתמש ב-Docker Hub
        FRONT_IMG     = "luxe-frontend"
        BACK_IMG      = "luxe-backend"
        AUTH_IMG      = "luxe-auth"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-credentials-id',
                    url: 'git@github.com:YOUR_ORG/Luxe-jewelery-store.git'
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh 'docker build -t $REGISTRY/$AUTH_IMG:latest -f ../infra/Dockerfile.auth .'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'docker build -t $REGISTRY/$BACK_IMG:latest -f ../infra/Dockerfile.backend .'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh 'docker build -t $REGISTRY/$FRONT_IMG:latest -f ../infra/Dockerfile.fronted .'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-credentials-id',   // משמש גם לגישה ל-Docker Hub אם מוגדר שם/סיסמה
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh 'docker push $REGISTRY/$AUTH_IMG:latest'
                    sh 'docker push $REGISTRY/$BACK_IMG:latest'
                    sh 'docker push $REGISTRY/$FRONT_IMG:latest'
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
    }
}
