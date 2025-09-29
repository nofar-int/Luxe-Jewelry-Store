pipeline {
    agent { label 'jenkins-agent' }

    options {
        timestamps()
    }

    environment {
        SNYK_TOKEN = credentials('SNYK_TOKEN')
    }

    stages {

        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git',
                    branch: 'main'
                )
            }
        }

        stage('Build Images') {
            steps {
                sh '''
                  DOCKER_BUILDKIT=0 docker build -t nofarpanker/luxe-auth:latest    -f infra/Dockerfile.auth     auth-service
                  DOCKER_BUILDKIT=0 docker build -t nofarpanker/luxe-backend:latest -f infra/Dockerfile.backend  backend
                  DOCKER_BUILDKIT=0 docker build -t nofarpanker/luxe-frontend:latest -f infra/Dockerfile.frontend jewelry-store
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker',
                                                 usernameVariable: 'DOCKER_USER',
                                                 passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                      echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                      docker push nofarpanker/luxe-auth:latest
                      docker push nofarpanker/luxe-backend:latest
                      docker push nofarpanker/luxe-frontend:latest
                    '''
                }
            }
        }

        stage('Snyk Monitor') {
            steps {
                sh '''
                  snyk container monitor nofarpanker/luxe-auth:latest    --file=infra/Dockerfile.auth
                  snyk container monitor nofarpanker/luxe-backend:latest --file=infra/Dockerfile.backend
                  snyk container monitor nofarpanker/luxe-frontend:latest --file=infra/Dockerfile.frontend
                '''
            }
        }
    }
}
