pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKERHUB_USER = "nofarpanker"
        GIT_CREDS     = credentials('github-credentials-id')
        FRONT_IMG     = "luxe-frontend"
        BACK_IMG      = "luxe-backend"
        AUTH_IMG      = "luxe-auth"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', credentialsId: "${GIT_CREDS}"
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('auth-service') {
                    sh "docker build -t ${DOCKERHUB_USER}/${AUTH_IMG} -f ../infra/Dockerfile.auth ."
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh "docker build -t ${DOCKERHUB_USER}/${BACK_IMG} -f ../infra/Dockerfile.backend ."
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('jewelry-store') {
                    sh "docker build -t ${DOCKERHUB_USER}/${FRONT_IMG} -f ../infra/Dockerfile.frontend ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "docker login -u ${DOCKERHUB_USER} -p ${DOCKER_HUB_PASS}"
                sh "docker push ${DOCKERHUB_USER}/${AUTH_IMG}"
                sh "docker push ${DOCKERHUB_USER}/${BACK_IMG}"
                sh "docker push ${DOCKERHUB_USER}/${FRONT_IMG}"
                sh "docker logout"
            }
        }
    }

    post {
        always {
            echo "Pipeline finished"
        }
        success {
            echo "All services built and pushed successfully!"
        }
        failure {
            echo "Pipeline failed. Check logs."
        }
    }
}


